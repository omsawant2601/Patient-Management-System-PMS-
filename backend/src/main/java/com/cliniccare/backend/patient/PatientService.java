package com.cliniccare.backend.patient;

import com.cliniccare.backend.common.ConflictException;
import com.cliniccare.backend.common.NotFoundException;
import com.cliniccare.backend.user.AppUser;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.util.UUID;

@Service
public class PatientService {
    private final PatientRepository patients;
    public PatientService(PatientRepository patients){this.patients=patients;}

    @Transactional
    public PatientDtos.Response create(PatientDtos.Request request, AppUser actor) {
        if(patients.findByMobile(request.mobile()).isPresent())
            throw new ConflictException("A patient is already registered with this mobile number");
        Patient p=new Patient();
        p.setPatientCode(Long.toString(patients.nextRegistrationNumber()));
        apply(p,request); p.setCreatedBy(actor); p.setUpdatedBy(actor);
        p.setRegistrationDate(LocalDate.now(ZoneId.of("Asia/Kolkata")));
        updateAge(p);
        patients.save(p);
        return toResponse(p);
    }

    @Transactional
    public PatientDtos.Response update(UUID id, PatientDtos.Request request, AppUser actor) {
        Patient p=patients.findById(id).orElseThrow(() -> new NotFoundException("Patient not found"));
        if (!p.getMobile().equals(request.mobile()) && patients.findByMobile(request.mobile()).isPresent())
            throw new ConflictException("A patient is already registered with this mobile number");
        apply(p,request); p.setUpdatedBy(actor); p.setUpdatedAt(Instant.now()); updateAge(p);
        patients.save(p);
        return toResponse(p);
    }

    public PatientDtos.Response get(UUID id) {
        return toResponse(patients.findById(id).orElseThrow(() -> new NotFoundException("Patient not found")));
    }

    public Page<PatientDtos.Response> list(int page,int size,String sort,String name,String mobile) {
        Pageable pageable=PageRequest.of(page,size,Sort.by(sort.split(",")[0]).descending());
        Page<Patient> result;
        if(name!=null&&!name.isBlank()) result=patients.findByFullNameContainingIgnoreCase(name,pageable);
        else if(mobile!=null&&!mobile.isBlank()) result=patients.findByMobileContaining(mobile,pageable);
        else result=patients.findAll(pageable);
        return result.map(this::toResponse);
    }

    public java.util.List<PatientDtos.Response> search(String name,String mobile) {
        Pageable one=PageRequest.of(0,100);
        Page<Patient> result=name!=null&&!name.isBlank()
                ? patients.findByFullNameContainingIgnoreCase(name,one)
                : patients.findByMobileContaining(mobile==null?"":mobile,one);
        return result.getContent().stream().map(this::toResponse).toList();
    }

    private void apply(Patient p,PatientDtos.Request r){
        p.setFullName(r.fullName()); p.setMobile(r.mobile());
        p.setAlternateContactNumber(r.alternateContactNumber()); p.setAddress(r.address());
        p.setDateOfBirth(r.dateOfBirth()); p.setGender(r.gender());
    }
    private void updateAge(Patient p){
        if(p.getDateOfBirth()!=null) p.setAge(Period.between(p.getDateOfBirth(),
                LocalDate.now(ZoneId.of("Asia/Kolkata"))).getYears());
    }
    public PatientDtos.Response toResponse(Patient p){
        updateAge(p);
        return new PatientDtos.Response(p.getId(),p.getPatientCode(),p.getFullName(),p.getMobile(),
                p.getAlternateContactNumber(),p.getAddress(),p.getDateOfBirth(),p.getAge(),
                p.getGender(),p.getRegistrationDate(),p.getProfilePhotoKey()!=null);
    }
}
