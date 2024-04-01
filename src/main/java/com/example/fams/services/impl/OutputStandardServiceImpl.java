package com.example.fams.services.impl;

import com.example.fams.config.CustomValidationException;
import com.example.fams.config.ResponseUtil;
import com.example.fams.converter.GenericConverter;
import com.example.fams.dto.OutputStandardDTO;
import com.example.fams.entities.*;
import com.example.fams.repository.ContentRepository;
import com.example.fams.repository.OutputStandardRepository;
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

    private final ContentRepository contentRepository;

    public OutputStandardServiceImpl(OutputStandardRepository outputStandardRepository, GenericConverter genericConverter, ContentRepository contentRepository) {
        this.outputStandardRepository = outputStandardRepository;
        this.genericConverter = genericConverter;
        this.contentRepository = contentRepository;
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
        Long requestContentId = outputStandardDTO.getContentId();

        if (requestContentId != null) {
            ServiceUtils.validateContentIds(List.of(requestContentId), contentRepository);
        }
        if (!ServiceUtils.errors.isEmpty()) {
            throw new CustomValidationException(ServiceUtils.errors);
        }
        if (outputStandardDTO.getId() != null){
            OutputStandard oldEntity = outputStandardRepository.findById(outputStandardDTO.getId());
            OutputStandard tempOldEntity = ServiceUtils.cloneFromEntity(oldEntity);
            outputStandard = convertDtoToEntity(outputStandardDTO, contentRepository);
            ServiceUtils.fillMissingAttribute(outputStandard, tempOldEntity);
            outputStandard.markModified();
            outputStandardRepository.save(outputStandard);
        } else {
            outputStandard = convertDtoToEntity(outputStandardDTO, contentRepository);
            outputStandard.setStatus(true);
            outputStandardRepository.save(outputStandard);
        }
        OutputStandardDTO result = convertOutputStandardToOutputStandardDTO(outputStandard);
        if (outputStandardDTO.getId() != null) {
            result.setContentId(outputStandard.getContent().getId());
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

    public OutputStandard convertDtoToEntity(OutputStandardDTO outputStandardDTO, ContentRepository contentRepository) {
        OutputStandard outputStandard = new OutputStandard();
        outputStandard.setId(outputStandardDTO.getId());
        outputStandard.setOutputStandardName(outputStandardDTO.getOutputStandardName());

        // Fetch the Syllabus using the provided syllabusId
        Content content = contentRepository.findById(outputStandardDTO.getContentId());
        outputStandard.setContent(content);

        return outputStandard;
    }

    private OutputStandardDTO convertOutputStandardToOutputStandardDTO(OutputStandard outputStandard) {
        OutputStandardDTO newDTO = (OutputStandardDTO) genericConverter.toDTO(outputStandard, OutputStandardDTO.class);
        if (outputStandard.getContent() == null) newDTO.setContentId(null);
        else newDTO.setContentId(outputStandard.getContent().getId());
        return newDTO;
    }
}
