package com.cliniccare.backend.prescription;

import com.cliniccare.backend.common.*;
import com.cliniccare.backend.consultation.MedicalRecordRepository;
import com.cliniccare.backend.patient.PatientRepository;
import com.cliniccare.backend.user.AppUser;
import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.time.*;
import java.util.*;

@Service
public class PrescriptionService {
    private final PrescriptionRepository prescriptions;
    private final MedicalRecordRepository records;
    private final PatientRepository patients;
    public PrescriptionService(PrescriptionRepository prescriptions,MedicalRecordRepository records,PatientRepository patients){
        this.prescriptions=prescriptions; this.records=records; this.patients=patients;
    }

    @Transactional
    public PrescriptionDtos.Response create(PrescriptionDtos.Request request,AppUser doctor){
        var record=records.findById(request.consultationId()).orElseThrow(() -> new NotFoundException("Consultation not found"));
        if(!record.getDoctor().getId().equals(doctor.getId())) throw new ForbiddenException("Only the treating doctor may prescribe");
        Prescription p=new Prescription();
        p.setPatient(record.getPatient()); p.setDoctor(doctor); p.setMedicalRecord(record);
        p.setPrescriptionNumber("RX-"+System.currentTimeMillis()); p.setInstructions(request.instructions());
        p.setStatus("DRAFT");
        for(var m:request.medicines()){
            PrescriptionItem i=new PrescriptionItem();
            i.setPrescription(p); i.setMedicineName(m.medicineName()); i.setDosage(m.dosage());
            i.setFrequency(m.frequency()); i.setDuration(m.duration()); i.setQuantity(m.quantity()); i.setInstructions(m.instructions());
            p.getItems().add(i);
        }
        prescriptions.save(p); return toResponse(p);
    }

    @Transactional
    public PrescriptionDtos.Response update(UUID id,PrescriptionDtos.Request request,AppUser doctor){
        Prescription p=prescriptions.findById(id).orElseThrow(() -> new NotFoundException("Prescription not found"));
        if(!p.getDoctor().getId().equals(doctor.getId())) throw new ForbiddenException("Only the prescribing doctor may edit");
        if("FINALIZED".equals(p.getStatus())) throw new ConflictException("Finalized prescriptions cannot be edited directly");
        p.setInstructions(request.instructions()); p.getItems().clear();
        for(var m:request.medicines()){
            PrescriptionItem i=new PrescriptionItem();
            i.setPrescription(p); i.setMedicineName(m.medicineName()); i.setDosage(m.dosage());
            i.setFrequency(m.frequency()); i.setDuration(m.duration()); i.setQuantity(m.quantity()); i.setInstructions(m.instructions());
            p.getItems().add(i);
        }
        p.setUpdatedAt(Instant.now()); prescriptions.save(p); return toResponse(p);
    }

    @Transactional
    public PrescriptionDtos.Response finalize(UUID id,AppUser doctor){
        Prescription p=prescriptions.findById(id).orElseThrow(() -> new NotFoundException("Prescription not found"));
        if(!p.getDoctor().getId().equals(doctor.getId())) throw new ForbiddenException("Only the prescribing doctor may finalize");
        p.setStatus("FINALIZED"); p.setFinalizedAt(Instant.now()); p.setUpdatedAt(Instant.now());
        prescriptions.save(p); return toResponse(p);
    }

    public PrescriptionDtos.Response get(UUID id){
        return toResponse(prescriptions.findById(id).orElseThrow(() -> new NotFoundException("Prescription not found")));
    }
    public List<PrescriptionDtos.Response> history(UUID patientId){
        return prescriptions.findByPatientIdOrderByPrescriptionDateDesc(patientId).stream().map(this::toResponse).toList();
    }

    public byte[] pdf(UUID id,String clinicName,String clinicContact) {
        Prescription p=prescriptions.findById(id).orElseThrow(() -> new NotFoundException("Prescription not found"));
        try(ByteArrayOutputStream out=new ByteArrayOutputStream()){
            Document doc=new Document(PageSize.A4);
            PdfWriter.getInstance(doc,out); doc.open();
            doc.add(new Paragraph(clinicName)); doc.add(new Paragraph(clinicContact));
            doc.add(new Paragraph("Prescription: "+p.getPrescriptionNumber()));
            doc.add(new Paragraph("Date: "+LocalDateTime.ofInstant(p.getPrescriptionDate(),ZoneId.of("Asia/Kolkata"))));
            doc.add(new Paragraph("Patient: "+p.getPatient().getFullName()+" ("+p.getPatient().getPatientCode()+")"));
            int age=p.getPatient().getAge()==null?0:p.getPatient().getAge();
            doc.add(new Paragraph("Age: "+age));
            doc.add(new Paragraph("Doctor: "+p.getDoctor().getFullName()));
            doc.add(Chunk.NEWLINE);
            PdfPTable table=new PdfPTable(5); table.setWidthPercentage(100);
            for(String h:new String[]{"Medicine","Dosage","Frequency","Duration","Instructions"}) table.addCell(new PdfPCell(new Phrase(h)));
            for(PrescriptionItem i:p.getItems()){
                table.addCell(i.getMedicineName()); table.addCell(i.getDosage());
                table.addCell(i.getFrequency()); table.addCell(i.getDuration()); table.addCell(i.getInstructions());
            }
            doc.add(table); doc.add(new Paragraph("Instructions: "+(p.getInstructions()==null?"":p.getInstructions())));
            doc.add(Chunk.NEWLINE); doc.add(new Paragraph("Doctor signature: __________________"));
            doc.close(); return out.toByteArray();
        }catch(Exception e){throw new IllegalStateException("Could not generate prescription PDF",e);}
    }

    private PrescriptionDtos.Response toResponse(Prescription p){
        var medicines=p.getItems().stream().map(i->new PrescriptionDtos.Medicine(i.getMedicineName(),i.getDosage(),
                i.getFrequency(),i.getDuration(),i.getQuantity(),i.getInstructions())).toList();
        return new PrescriptionDtos.Response(p.getId(),p.getPrescriptionNumber(),p.getPatient().getId(),
                p.getDoctor().getId(),p.getMedicalRecord().getId(),p.getPrescriptionDate(),
                p.getInstructions(),p.getStatus(),medicines);
    }
}
