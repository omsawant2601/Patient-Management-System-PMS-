package com.cliniccare.backend.appointment;

import com.cliniccare.backend.common.*;
import com.cliniccare.backend.patient.*;
import com.cliniccare.backend.reminder.*;
import com.cliniccare.backend.user.*;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.util.*;

@Service
public class AppointmentService {
    private final AppointmentRepository appointments;
    private final PatientRepository patients;
    private final AppUserRepository users;
    private final AppointmentReminderRepository reminders;
    public AppointmentService(AppointmentRepository appointments,PatientRepository patients,
                              AppUserRepository users,AppointmentReminderRepository reminders){
        this.appointments=appointments;this.patients=patients;this.users=users;this.reminders=reminders;
    }

    @Transactional
    public AppointmentDtos.Response schedule(AppointmentDtos.Request r,AppUser actor){
        Patient patient=patients.findById(r.patientId()).orElseThrow(() -> new NotFoundException("Patient not found"));
        AppUser doctor=users.findById(r.doctorId()).orElseThrow(() -> new NotFoundException("Doctor not found"));
        if(doctor.getRole()!=AppUser.Role.DOCTOR) throw new ConflictException("Selected user is not a doctor");
        Instant end=r.scheduledAt().plusSeconds(r.durationMinutes()*60L);
        if(!appointments.findOverlapping(doctor.getId(),r.scheduledAt(),end).isEmpty())
            throw new ConflictException("The doctor already has an appointment in this time range");
        Appointment a=new Appointment();
        a.setAppointmentNumber("APT-"+System.currentTimeMillis());a.setPatient(patient);a.setDoctor(doctor);
        a.setScheduledAt(r.scheduledAt());a.setDurationMinutes(r.durationMinutes());
        a.setAppointmentType(r.appointmentType());a.setNotes(r.notes());a.setCreatedBy(actor);
        appointments.save(a); scheduleReminders(a); return toResponse(a);
    }

    @Transactional
    public AppointmentDtos.Response reschedule(UUID id,AppointmentDtos.RescheduleRequest r,AppUser actor){
        Appointment a=appointments.findById(id).orElseThrow(() -> new NotFoundException("Appointment not found"));
        if(Set.of("CANCELLED","COMPLETED").contains(a.getStatus())) throw new ConflictException("Appointment cannot be rescheduled");
        Instant end=r.scheduledAt().plusSeconds(r.durationMinutes()*60L);
        var overlap=appointments.findOverlapping(a.getDoctor().getId(),r.scheduledAt(),end);
        if(overlap.stream().anyMatch(x->!x.getId().equals(id)))
            throw new ConflictException("The doctor already has an appointment in this time range");
        a.setScheduledAt(r.scheduledAt());a.setDurationMinutes(r.durationMinutes());a.setNotes(r.notes());a.setUpdatedAt(Instant.now());
        appointments.save(a);
        for(AppointmentReminder x:reminders.findByAppointmentId(id)){
            if("PENDING".equals(x.getStatus())||"FAILED".equals(x.getStatus())||"PROCESSING".equals(x.getStatus())) x.setStatus("CANCELLED");
        }
        scheduleReminders(a);return toResponse(a);
    }

    @Transactional
    public AppointmentDtos.Response cancel(UUID id,AppUser actor){
        Appointment a=appointments.findById(id).orElseThrow(() -> new NotFoundException("Appointment not found"));
        a.setStatus("CANCELLED");a.setUpdatedAt(Instant.now());appointments.save(a);
        reminders.findByAppointmentId(id).forEach(r->{if(!"SENT".equals(r.getStatus())&&! "DELIVERED".equals(r.getStatus())&&! "READ".equals(r.getStatus()))r.setStatus("CANCELLED");});
        return toResponse(a);
    }

    @Transactional
    public AppointmentDtos.Response status(UUID id,AppointmentDtos.StatusRequest r,AppUser actor){
        Appointment a=appointments.findById(id).orElseThrow(() -> new NotFoundException("Appointment not found"));
        a.setStatus(r.status().name());a.setUpdatedAt(Instant.now());appointments.save(a);return toResponse(a);
    }

    public AppointmentDtos.Response get(UUID id){return toResponse(appointments.findById(id).orElseThrow(() -> new NotFoundException("Appointment not found"))); }
    public Page<AppointmentDtos.Response> list(int page,int size,UUID patientId,UUID doctorId,Instant from,Instant to){
        Pageable p=PageRequest.of(page,size,Sort.by("scheduledAt").ascending());
        Page<Appointment> result;
        if(patientId!=null)result=appointments.findByPatientId(patientId,p);
        else if(doctorId!=null)result=appointments.findByDoctorId(doctorId,p);
        else result=appointments.findByScheduledAtBetween(from==null?Instant.EPOCH:from,to==null?Instant.now().plus(Duration.ofDays(365)):to,p);
        return result.map(this::toResponse);
    }

    private void scheduleReminders(Appointment a){
        createReminder(a,"24_HOUR",a.getScheduledAt().minusSeconds(86400));
        createReminder(a,"1_HOUR",a.getScheduledAt().minusSeconds(3600));
    }
    private void createReminder(Appointment a,String type,Instant scheduled){
        var existing=reminders.findByAppointmentIdAndReminderType(a.getId(),type);
        if(existing.isPresent())return;
        AppointmentReminder r=new AppointmentReminder();
        r.setAppointment(a);r.setReminderType(type);r.setScheduledAt(scheduled);r.setStatus("PENDING");
        reminders.save(r);
    }
    private AppointmentDtos.Response toResponse(Appointment a){
        return new AppointmentDtos.Response(a.getId(),a.getAppointmentNumber(),a.getPatient().getId(),
                a.getDoctor()==null?null:a.getDoctor().getId(),a.getScheduledAt(),a.getDurationMinutes(),
                a.getAppointmentType(),a.getStatus(),a.getNotes());
    }
}
