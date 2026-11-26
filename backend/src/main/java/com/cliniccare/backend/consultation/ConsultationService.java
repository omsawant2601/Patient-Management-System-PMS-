package com.cliniccare.backend.consultation;

import com.cliniccare.backend.appointment.Appointment;
import com.cliniccare.backend.appointment.AppointmentRepository;
import com.cliniccare.backend.common.*;
import com.cliniccare.backend.patient.PatientRepository;
import com.cliniccare.backend.user.AppUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class ConsultationService {
    private final MedicalRecordRepository records;
    private final PatientRepository patients;
    private final AppointmentRepository appointments;
    public ConsultationService(MedicalRecordRepository r,PatientRepository p,AppointmentRepository a){records=r;patients=p;appointments=a;}

    @Transactional
    public ConsultationDtos.Response create(ConsultationDtos.Request request, AppUser doctor) {
        if(doctor.getRole()!=AppUser.Role.DOCTOR) throw new ForbiddenException("Doctors only");
        MedicalRecord r=new MedicalRecord();
        r.setPatient(patients.findById(request.patientId()).orElseThrow(() -> new NotFoundException("Patient not found")));
        r.setDoctor(doctor);
        if(request.appointmentId()!=null) r.setAppointment(appointments.findById(request.appointmentId()).orElseThrow(() -> new NotFoundException("Appointment not found")));
        r.setVisitDate(request.consultationDate()==null?Instant.now():request.consultationDate());
        r.setSymptoms(request.symptoms()); r.setDiagnosis(request.diagnosis());
        r.setClinicalNotes(request.clinicalNotes()); r.setFollowUpRecommendations(request.followUpRecommendations());
        r.setStatus("DRAFT"); records.save(r); return toResponse(r);
    }

    @Transactional
    public ConsultationDtos.Response update(UUID id,ConsultationDtos.Request request,AppUser doctor) {
        MedicalRecord r=records.findById(id).orElseThrow(() -> new NotFoundException("Consultation not found"));
        if(r.getDoctor().getId().equals(doctor.getId())==false) throw new ForbiddenException("Only the treating doctor may edit");
        if("FINALIZED".equals(r.getStatus())) throw new ConflictException("Finalized consultation records cannot be edited directly");
        r.setSymptoms(request.symptoms()); r.setDiagnosis(request.diagnosis());
        r.setClinicalNotes(request.clinicalNotes()); r.setFollowUpRecommendations(request.followUpRecommendations());
        r.setUpdatedAt(Instant.now()); records.save(r); return toResponse(r);
    }

    public ConsultationDtos.Response get(UUID id){return toResponse(records.findById(id).orElseThrow(() -> new NotFoundException("Consultation not found"))); }
    public List<ConsultationDtos.Response> history(UUID patientId){return records.findByPatientIdOrderByVisitDateDesc(patientId).stream().map(this::toResponse).toList();}
    private ConsultationDtos.Response toResponse(MedicalRecord r){
        return new ConsultationDtos.Response(r.getId(),r.getPatient().getId(),r.getDoctor().getId(),
                r.getAppointment()==null?null:r.getAppointment().getId(),r.getVisitDate(),r.getSymptoms(),
                r.getDiagnosis(),r.getClinicalNotes(),r.getFollowUpRecommendations(),r.getStatus(),
                r.getDoctor().getFullName());
    }
}
