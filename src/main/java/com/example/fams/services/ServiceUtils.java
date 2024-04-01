package com.example.fams.services;

import com.example.fams.dto.ClassDTO;
import com.example.fams.entities.FamsClass;
import com.example.fams.repository.*;
import org.apache.commons.beanutils.BeanUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class ServiceUtils {
    public static List<String> errors = new ArrayList<>();

    public static <T> T fillMissingAttribute(T entity, T tempOldEntity) {
        List<Field> allFields = new ArrayList<>();
        Class<?> currentClass = entity.getClass();
        try {
            // Traverse class hierarchy to collect fields from all superclasses
            while (currentClass != null) {
                Field[] declaredFields = currentClass.getDeclaredFields();
                allFields.addAll(Arrays.asList(declaredFields));
                currentClass = currentClass.getSuperclass();
            }

            // Iterate over all fields
            for (Field field : allFields) {
                field.setAccessible(true); // Enable access to private fields if any

                try {
                    Object newValue = field.get(entity); // Get the value of the field for the newEntity
                    if (newValue == null) {
                        // If the value is null, get the corresponding value from oldEntity
                        Object oldValue = field.get(tempOldEntity);
                        field.set(entity, oldValue); // Set the value of the field for the newEntity
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            return entity;
        } catch (Exception e) {
            throw e;
        }
    }

    public static <T> T cloneFromEntity(T t) {
        T clone;
        try {
            clone = (T) t.getClass().newInstance();
            BeanUtils.copyProperties(clone, t);
        } catch (IllegalAccessException | InvocationTargetException | InstantiationException e) {
            e.printStackTrace(); // Handle the exception appropriately
            return null;
        }
        return clone;
    }

    public static void validateContentIds(List<Long> contentIds, ContentRepository contentRepository) {
        for (Long contentId : contentIds) {
            if (contentRepository.findById(contentId) == null) {
                errors.add("Content with id " + contentId + " does not exist");
            }
        }
    }



    public static void validateLearningObjectiveIds(List<Long> learningObjectiveIds, LearningObjectiveRepository learningObjectiveRepository) {
        for (Long learningObjectiveId : learningObjectiveIds) {
            if (learningObjectiveRepository.findById(learningObjectiveId) == null) {
                errors.add("LearningObjective with id " + learningObjectiveId + " does not exist");
            }
        }
    }



    public static void validateSyllabusIds(List<Long> syllabusIds, SyllabusRepository syllabusRepository) {
        for (Long syllabusId : syllabusIds) {
            if (syllabusRepository.findOneById(syllabusId) == null) {
                errors.add("Syllabus with id " + syllabusIds + " does not exist");
            }
        }
    }


    public static void validateUnitIds(List<Long> unitIds, UnitRepository unitRepository) {
        for (Long unitId : unitIds) {
            if (unitRepository.findById(unitId) == null) {
                errors.add("Unit with id " + unitId + " does not exist");
            }
        }
    }
    public static void validateUserIds(List<Long> userIds, UserRepository userRepository) {
        for (Long userId : userIds) {
            if (userRepository.findById(userId) == null) {
                errors.add("User with id " + userId + " does not exist");
            }
        }
    }

    public static void validateTrainingProgramIds(List<Long> trainingProgramIds, TrainingProgramRepository trainingProgramRepository) {
        for (Long trainingProgramId : trainingProgramIds) {
            if (trainingProgramRepository.findById(trainingProgramId).isEmpty()) {
                errors.add("Training Program with id " + trainingProgramId + " does not exist");
            }
        }
    }

    public static void validateMaterialIds(List<Long> materialIds, MaterialRepository materialRepository) {
        for (Long materialId : materialIds) {
            if (materialRepository.findById(materialId) == null) {
                errors.add("Material with id " + materialId + " does not exist");
            }
        }
    }

    public static void validateClassIds(List<Long> classIds, ClassRepository classRepository) {
        for (Long classId : classIds) {
            if (classRepository.findById(classId) == null) {
                errors.add("Class with id " + classId + " does not exist");
            }
        }
    }

    public static void validateOutputStandardIds(List<Long> outputStandardIds, OutputStandardRepository outputStandardRepository) {
        for (Long outputStandardId : outputStandardIds) {
            if (outputStandardRepository.findById(outputStandardId) == null) {
                errors.add("OutputStandard with id " + outputStandardId + " does not exist");
            }
        }
    }

    public static void validateStartDateBeforeEndDate(ClassDTO classDTO) {
        Long startDate = classDTO.getStartDate();
        Long endDate = classDTO.getEndDate();
        if (endDate <= startDate){
            errors.add("Class start Date must be before end date");
        }
    }

    public static void validateStartDateWhenSameTimeFrame(ClassDTO classDTO, ClassRepository classRepository) {
        List<FamsClass> conflictDateRangeClasses = classRepository.findFamsClassWithStartDateInRange(classDTO.getStartDate());
        if (conflictDateRangeClasses != null){
            conflictDateRangeClasses.forEach(famsClass -> {
                if (Objects.equals(famsClass.getStartTimeFrame(), classDTO.getStartTimeFrame())){
                    errors.add(famsClass.getName() + " with this time frame has existed!");
                }
            });
        }
    }
}