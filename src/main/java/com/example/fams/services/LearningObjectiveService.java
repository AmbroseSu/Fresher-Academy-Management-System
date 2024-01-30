package com.example.fams.services;

import com.example.fams.entities.LearningObjective;
import com.example.fams.repository.LearningObjectiveRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LearningObjectiveService {

        @Autowired
        private LearningObjectiveRepository learningObjectiveRepository;

        public List<LearningObjective> getAllLearningObjectives() {
            return learningObjectiveRepository.findAll();
        }

        public LearningObjective getLearningObjectiveByCode(String code) {
            return learningObjectiveRepository.findById(code).orElse(null);
        }

        public LearningObjective createLearningObjective(LearningObjective learningObjective) {
            return learningObjectiveRepository.save(learningObjective);
        }

        public LearningObjective updateLearningObjective(String code, LearningObjective updatedLearningObjective) {
            if (learningObjectiveRepository.existsById(code)) {
                updatedLearningObjective.setCode(code);
                return learningObjectiveRepository.save(updatedLearningObjective);
            }
            return null;
        }

        public void deleteLearningObjective(String code) {
            learningObjectiveRepository.deleteById(code);
        }

}
