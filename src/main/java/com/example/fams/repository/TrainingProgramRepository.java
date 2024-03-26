package com.example.fams.repository;

import com.example.fams.entities.Syllabus;
import com.example.fams.entities.TrainingProgram;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Repository
public interface TrainingProgramRepository extends JpaRepository<TrainingProgram, Long> {

    List<TrainingProgram> findAllByStatusIsTrue(Pageable pageable);

//    @Query("SELECT tp FROM TrainingProgram tp " +
//            "ORDER BY " +
//            "CASE WHEN :orderBy ='cDESC' THEN tp.createdDate END DESC, " +
//            "CASE WHEN :orderBy ='cASC' THEN tp.createdDate END ASC, " +
//            "tp.createdDate DESC")
//    List<TrainingProgram> findAll(Pageable pageable, String orderBy);
    List<TrainingProgram> findAllByOrderByIdDesc(Pageable pageable);
    TrainingProgram findOneById(Long id);
    TrainingProgram findByStatusIsTrueAndId(Long id);
    TrainingProgram findByStatusIsTrueAndName(String name);
    Long countAllByStatusIsTrue();

    @Transactional
    @Modifying
    @Query("UPDATE TrainingProgram tp SET tp.status = ?1 WHERE tp.id = ?2")
    void changeStatus(Boolean status, Long id);


    @Query("SELECT TP FROM TrainingProgram TP WHERE TP.id = :id AND TP.status = TRUE ")
    List<TrainingProgram> getAllTrainingProgramById(@Param("id") Long id);

    @Query("SELECT TP FROM TrainingProgram TP WHERE TP.name = :name AND TP.status = TRUE ")
    List<TrainingProgram> getAllTrainingProgramByName(@Param("name") String name);

    @Query("SELECT TP FROM TrainingProgram TP WHERE TP.id = :id AND TP.name = :name AND TP.status = TRUE ")
    List<TrainingProgram> getAllSyllabusByNameAndId(@Param("id") Long id, @Param("name") String name);


    @Query("SELECT tp FROM TrainingProgram tp " +
            "WHERE (:name IS NULL OR tp.name = :name) AND tp.status = TRUE " +
            "AND (:startTime IS NULL OR tp.startTime = :startTime) " +
            "AND (:duration IS NULL OR tp.duration = :duration) " +
            "AND (:training_status IS NULL OR tp.training_status = :training_status)" +
            "ORDER BY " +
            "CASE WHEN :sort_by_created_date ='cDESC' THEN tp.createdDate END DESC, " +
            "CASE WHEN :sort_by_created_date ='cASC' THEN tp.createdDate END ASC, " +
            "tp.createdDate DESC")
    List<TrainingProgram> searchSortFilter(@Param("name") String name,
                                           @Param("startTime") Long startTime,
                                           @Param("duration") Long duration,
                                           @Param("training_status") Integer training_status,
                                           @Param("sort_by_created_date") String sortByCreatedDate,
                                           Pageable pageable);

    @Query("SELECT COUNT(tp) FROM TrainingProgram tp " +
            "WHERE (:name IS NULL OR tp.name = :name) AND tp.status = TRUE " +
            "AND (:startTime IS NULL OR tp.startTime = :startTime) " +
            "AND (:duration IS NULL OR tp.duration = :duration) " +
            "AND (:training_status IS NULL OR tp.training_status = :training_status)")
    Long countSearchSortFilter(@Param("name") String name,
                               @Param("startTime") Long startTime,
                               @Param("duration") Long duration,
@Param("training_status") Integer training_status);



    @Query("SELECT tp FROM TrainingProgram tp " +
            "WHERE (:name IS NULL OR tp.name = :name) " +
            "AND (:startTime IS NULL OR tp.startTime = :startTime) " +
            "AND (:duration IS NULL OR tp.duration = :duration) " +
            "AND (:training_status IS NULL OR tp.training_status = :training_status) " +
            "ORDER BY " +
            "CASE WHEN :sortById ='iDESC' THEN tp.id END DESC, " +
            "CASE WHEN :sortById ='iASC' THEN tp.id END ASC, " +
            "tp.id DESC")
    List<TrainingProgram> searchSortFilterADMIN(@Param("name") String name,
                                                @Param("startTime") Long startTime,
                                                @Param("duration") Long duration,
                                                @Param("training_status") Integer training_status,
                                                @Param("sortById") String sortById,
                                                Pageable pageable);
}
