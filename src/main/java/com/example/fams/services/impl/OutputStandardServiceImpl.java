package com.example.fams.services.impl;

import com.example.fams.config.CustomValidationException;
import com.example.fams.config.ResponseUtil;
import com.example.fams.converter.GenericConverter;
import com.example.fams.dto.OutputStandardDTO;
import com.example.fams.entities.*;
import com.example.fams.repository.OutputStandardRepository;
import com.example.fams.repository.SyllabusRepository;
import com.example.fams.services.IOutputStandardService;
import com.example.fams.services.ServiceUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("OutputStandardService")
public class OutputStandardServiceImpl implements IOutputStandardService {

    private final OutputStandardRepository outputStandardRepository;

    private final GenericConverter genericConverter;

    private final SyllabusRepository syllabusRepository;

    public OutputStandardServiceImpl(OutputStandardRepository outputStandardRepository, GenericConverter genericConverter, SyllabusRepository syllabusRepository) {
        this.outputStandardRepository = outputStandardRepository;
        this.genericConverter = genericConverter;
        this.syllabusRepository = syllabusRepository;
    }

    @Override
    public ResponseEntity<?> findById(Long id) {
        OutputStandard entity = outputStandardRepository.findByStatusIsTrueAndId(id);
        if (entity != null) {
            OutputStandardDTO result = convertOutputStandardToOutputStandardDTO(entity);
            return ResponseUtil.getObject(result, HttpStatus.OK, "Fetched successfully");
        }
        return ResponseUtil.error("Output standard not found", "Cannot Find Output standard", HttpStatus.NOT_FOUND);
    }

    @Override
    public ResponseEntity<?> findAllByStatusTrue(int page, int limit) {
        return null;
    }

    @Override
    public ResponseEntity<?> findAll(int page, int limit) {
        return null;
    }

    @Override
    public ResponseEntity<?> save(OutputStandardDTO outputStandardDTO) {
        ServiceUtils.errors.clear();
        OutputStandard outputStandard;
        Long requestSyllabusId = outputStandardDTO.getSyllabusId();

        if (requestSyllabusId != null) {
            ServiceUtils.validateSyllabusIds(List.of(requestSyllabusId), syllabusRepository);
        }
        if (!ServiceUtils.errors.isEmpty()) {
            throw new CustomValidationException(ServiceUtils.errors);
        }
        if (outputStandardDTO.getId() != null){
            OutputStandard oldEntity = outputStandardRepository.findById(outputStandardDTO.getId());
            OutputStandard tempOldEntity = ServiceUtils.cloneFromEntity(oldEntity);
            outputStandard = convertDtoToEntity(outputStandardDTO, syllabusRepository);
            ServiceUtils.fillMissingAttribute(outputStandard, tempOldEntity);
            outputStandard.markModified();
            outputStandardRepository.save(outputStandard);
        } else {
            outputStandard = convertDtoToEntity(outputStandardDTO, syllabusRepository);
            outputStandard.setStatus(true);
            outputStandardRepository.save(outputStandard);
        }
        OutputStandardDTO result = convertOutputStandardToOutputStandardDTO(outputStandard);
        if (outputStandardDTO.getId() != null) {
            result.setSyllabusId(outputStandard.getSyllabus().getId());
        }
        return ResponseUtil.getObject(result, HttpStatus.OK, "Saved successfully");
    }

    @Override
    public ResponseEntity<?> changeStatus(Long id) {
        return null;
    }

    @Override
    public Boolean checkExist(Long id) {
        return null;
    }

    public OutputStandard convertDtoToEntity(OutputStandardDTO outputStandardDTO, SyllabusRepository syllabusRepository) {
        OutputStandard outputStandard = new OutputStandard();
        outputStandard.setId(outputStandardDTO.getId());
        outputStandard.setOutputStandardName(outputStandardDTO.getOutputStandardName());

        // Fetch the Syllabus using the provided syllabusId
        Syllabus syllabus = syllabusRepository.findOneById(outputStandardDTO.getSyllabusId());
        outputStandard.setSyllabus(syllabus);

        return outputStandard;
    }

    private OutputStandardDTO convertOutputStandardToOutputStandardDTO(OutputStandard outputStandard) {
        OutputStandardDTO newDTO = (OutputStandardDTO) genericConverter.toDTO(outputStandard, OutputStandardDTO.class);
        if (outputStandard.getSyllabus() == null) newDTO.setSyllabusId(null);
        else newDTO.setSyllabusId(outputStandard.getSyllabus().getId());
        return newDTO;
    }
}
