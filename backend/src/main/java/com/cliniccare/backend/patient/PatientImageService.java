package com.cliniccare.backend.patient;

import com.cliniccare.backend.common.NotFoundException;
import com.cliniccare.backend.common.BadRequestException;
import com.cliniccare.backend.user.AppUser;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Service
public class PatientImageService {
    private final PatientRepository patients;
    private final PatientPhotoComparisonRepository comparisons;
    private final PatientService patientService;
    private final PatientImageStorage storage;

    public PatientImageService(PatientRepository patients,PatientPhotoComparisonRepository comparisons,
                              PatientService patientService,PatientImageStorage storage) {
        this.patients=patients;
        this.comparisons=comparisons;
        this.patientService=patientService;
        this.storage=storage;
    }

    public PatientDtos.Response replaceProfilePhoto(UUID patientId,MultipartFile photo) {
        Patient patient=findPatient(patientId);
        String previousKey=patient.getProfilePhotoKey();
        String newKey=storage.store(photo);
        patient.setProfilePhotoKey(newKey);
        Patient saved;
        try {
            saved=patients.save(patient);
        } catch(RuntimeException ex) {
            cleanup(newKey,ex);
            throw ex;
        }
        if(previousKey!=null)storage.delete(previousKey);
        return patientService.toResponse(saved);
    }

    public void removeProfilePhoto(UUID patientId) {
        Patient patient=findPatient(patientId);
        String previousKey=patient.getProfilePhotoKey();
        if(previousKey==null)return;
        patient.setProfilePhotoKey(null);
        patients.save(patient);
        storage.delete(previousKey);
    }

    public PatientImageStorage.StoredImage profilePhoto(UUID patientId) {
        String key=findPatient(patientId).getProfilePhotoKey();
        if(key==null)throw new NotFoundException("Patient profile picture not found");
        return storage.open(key);
    }

    public List<PatientImageDtos.Comparison> gallery(UUID patientId) {
        findPatient(patientId);
        return comparisons.findByPatientIdOrderByCreatedAtDesc(patientId).stream()
                .map(item->new PatientImageDtos.Comparison(item.getId(),item.getCaption(),item.getCreatedAt()))
                .toList();
    }

    public PatientImageDtos.Comparison addComparison(UUID patientId,MultipartFile before,MultipartFile after,
                                                     String caption,AppUser actor) {
        if(caption!=null&&caption.length()>500)
            throw new com.cliniccare.backend.common.BadRequestException("Caption must be 500 characters or fewer");
        Patient patient=findPatient(patientId);
        String beforeKey=storage.store(before);
        String afterKey;
        try {
            afterKey=storage.store(after);
        } catch(RuntimeException ex) {
            cleanup(beforeKey,ex);
            throw ex;
        }
        PatientPhotoComparison comparison=new PatientPhotoComparison();
        comparison.setPatient(patient);
        comparison.setBeforeImageKey(beforeKey);
        comparison.setAfterImageKey(afterKey);
        comparison.setCaption(caption==null||caption.isBlank()?null:caption.trim());
        comparison.setCreatedBy(actor);
        try {
            PatientPhotoComparison saved=comparisons.save(comparison);
            return new PatientImageDtos.Comparison(saved.getId(),saved.getCaption(),saved.getCreatedAt());
        } catch(RuntimeException ex) {
            cleanup(beforeKey,ex);
            cleanup(afterKey,ex);
            throw ex;
        }
    }

    public PatientImageStorage.StoredImage comparisonImage(UUID patientId,UUID comparisonId,String side) {
        PatientPhotoComparison comparison=comparisons.findByIdAndPatientId(comparisonId,patientId)
                .orElseThrow(()->new NotFoundException("Patient photo comparison not found"));
        return switch(side) {
            case "before" -> storage.open(comparison.getBeforeImageKey());
            case "after" -> storage.open(comparison.getAfterImageKey());
            default -> throw new BadRequestException("Image side must be before or after");
        };
    }

    public void removeComparison(UUID patientId,UUID comparisonId) {
        PatientPhotoComparison comparison=comparisons.findByIdAndPatientId(comparisonId,patientId)
                .orElseThrow(()->new NotFoundException("Patient photo comparison not found"));
        comparisons.delete(comparison);
        storage.delete(comparison.getBeforeImageKey());
        storage.delete(comparison.getAfterImageKey());
    }

    private Patient findPatient(UUID id) {
        return patients.findById(id).orElseThrow(()->new NotFoundException("Patient not found"));
    }

    private void cleanup(String key,RuntimeException original) {
        try {
            storage.delete(key);
        } catch(RuntimeException cleanupFailure) {
            original.addSuppressed(cleanupFailure);
        }
    }
}
