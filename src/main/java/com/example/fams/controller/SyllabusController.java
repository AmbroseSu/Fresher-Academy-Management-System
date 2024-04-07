package com.example.fams.controller;

import com.example.fams.config.ResponseUtil;
import com.example.fams.dto.SyllabusDTO;
import com.example.fams.dto.request.DeleteReplace;
import com.example.fams.entities.enums.DuplicateHandle;
import com.example.fams.services.ISyllabusService;
import jakarta.validation.Valid;
import java.io.IOException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1")
@CrossOrigin
public class SyllabusController {

    @Autowired
    @Qualifier("SyllabusService")
    private ISyllabusService syllabusService;


    @PreAuthorize("hasAuthority('syllabus:Full_Access') || hasAuthority('syllabus:View')")
    @GetMapping("/syllabus/{id}")
    public ResponseEntity<?> getBySyllabusId(@PathVariable Long id) {
        return syllabusService.findById(id);
    }

    @PreAuthorize("hasAuthority('syllabus:Full_Access') || hasAuthority('syllabus:View')")
    @GetMapping("/syllabus")
    public ResponseEntity<?> getAllSyllabusByStatusTrue(@RequestParam(defaultValue = "1") int page,
                                                        @RequestParam(defaultValue = "10") int limit) {
        return syllabusService.findAllByStatusTrue(page, limit);
    }

    @PreAuthorize("hasAuthority('syllabus:Full_Access')")
    @GetMapping("/syllabus/hidden")
    public ResponseEntity<?> getAllSyllabus(@RequestParam(defaultValue = "1") int page,
                                            @RequestParam(defaultValue = "10") int limit) {
        return syllabusService.findAll(page, limit);
    }

    @PreAuthorize("hasAuthority('syllabus:Full_Access') || hasAuthority('syllabus:View')")
    @PostMapping("/syllabus/search")
    public ResponseEntity<?> searchSyllabus(@RequestBody SyllabusDTO syllabusDTO,
                                            @RequestParam(required = false) String sortByCreatedDate,
                                            @RequestParam(defaultValue = "1") int page,
                                            @RequestParam(defaultValue = "10") int limit){
        return syllabusService.searchSortFilter(syllabusDTO, sortByCreatedDate, page, limit);
    }

    @PreAuthorize("hasAuthority('syllabus:Full_Access')")
    @PostMapping("/syllabus/search/hidden")
    public ResponseEntity<?> searchSyllabusADMIN(@RequestBody SyllabusDTO syllabusDTO,
                                                 @RequestParam(required = false) String sortById,
                                                 @RequestParam(defaultValue = "1") int page,
                                                 @RequestParam(defaultValue = "10") int limit){
        return syllabusService.searchSortFilterADMIN(syllabusDTO, sortById, page, limit);
    }

    @PreAuthorize("hasAuthority('syllabus:Full_Access') || hasAuthority('syllabus:Create')")
    @PostMapping("/syllabus")
    public ResponseEntity<?> createSyllabus(@Valid @RequestBody SyllabusDTO syllabusDTO) {
        return syllabusService.save(syllabusDTO);
    }
    @PreAuthorize("hasAuthority('syllabus:Full_Access') || hasAuthority('syllabus:Modify')")
    @PutMapping("/syllabus/{id}")
    public ResponseEntity<?> updateSyllabus(@Valid @RequestBody SyllabusDTO syllabusDTO, @PathVariable(name = "id") Long id) {

        if(syllabusService.checkExist(id)){
            syllabusDTO.setId(id);
           return syllabusService.save(syllabusDTO);
        }
        return ResponseUtil.error("Not found","Syllabus not exist", HttpStatus.NOT_FOUND);
    }
    @PreAuthorize("hasAuthority('syllabus:Full_Access')")
    @DeleteMapping("/syllabus/{id}")
    public ResponseEntity<?> changeStatus(@PathVariable Long id) {
        return syllabusService.changeStatus(id);
    }

//    @PostMapping("syllabus/update")
//    public /*@ResponseBody*/ ResponseEntity<?> uploadFile(/*@RequestParam("file")*/@RequestBody MultipartFile file) {
//
//
//        try {
//            List<SyllabusDTO> syllabusDTOS = syllabusService.parseExcelFile(file);
//            for(SyllabusDTO syllabusDTO : syllabusDTOS) {
//                syllabusService.save(syllabusDTO);
//            }
//            return ResponseUtil.getObject(syllabusDTOS,HttpStatus.CREATED,"Upload Successfully!");
//        } catch (Exception e) {
//            String result = e.getMessage().toString();
//            return ResponseUtil.error(result, "",HttpStatus.BAD_REQUEST);
//        }
//    }

    @PreAuthorize("hasAuthority('syllabus:Full_Access') || hasAuthority('syllabus:Create')")
    @PostMapping("syllabus/updatee")
    public /*@ResponseBody*/ ResponseEntity<?> uploadFileReplace(/*@RequestParam("file")*/@RequestBody MultipartFile file,
        @RequestParam Boolean name, @RequestParam Boolean code, @RequestParam
    DuplicateHandle duplicateHandle) {


        try {
            if(duplicateHandle.toString().equals("REPLACE")){
                return syllabusService.checkSyllabusReplace(file, name, code);
            }else{
                if(duplicateHandle.toString().equals("SKIP")){
                    return syllabusService.checkSyllabusSkip(file, name, code);
                }else{
                    if(duplicateHandle.toString().equals("ALLOW")){
                        List<SyllabusDTO> syllabusDTOS = syllabusService.parseExcelFile(file);
                        for(SyllabusDTO syllabusDTO : syllabusDTOS) {
                            syllabusService.save(syllabusDTO);
                        }
                        return ResponseUtil.getObject(null, HttpStatus.OK, "Saved successfully");
                    }
                }
            }

            return ResponseUtil.error("Import False", "Import Syllabus False",HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            String result = e.getMessage().toString();
            return ResponseUtil.error(result, "",HttpStatus.BAD_REQUEST);
        }
    }



    @PreAuthorize("hasAuthority('syllabus:Full_Access') || hasAuthority('syllabus:Create')")
    @PostMapping("syllabus/updatecsv")
    public ResponseEntity<?> uploadFileReplaceNew(@RequestParam("file") MultipartFile file,
        @RequestParam Boolean name,
        @RequestParam Boolean code,
        @RequestParam DuplicateHandle duplicateHandle
    ) throws IOException {
        String s = syllabusService.checkCsvFile(file).getStatusCode().toString();

        try {
            if(duplicateHandle.toString().equals("REPLACE")){
                return syllabusService.checkSyllabusReplace(file, name, code);
            }else{
                if(duplicateHandle.toString().equals("SKIP")){
                    return syllabusService.checkSyllabusSkip(file, name, code);
                }else{
                    if(duplicateHandle.toString().equals("ALLOW")){

                        if(syllabusService.checkCsvFile(file).getStatusCode().toString().equals("200 OK")){
                            List<SyllabusDTO> syllabusDTOS = syllabusService.parseCsvFile(file);
                            for(SyllabusDTO syllabusDTO : syllabusDTOS) {
                                syllabusService.save(syllabusDTO);
                            }
                            return ResponseUtil.getObject(null, HttpStatus.OK, "Saved successfully");
                        }else{
                            return ResponseUtil.error("Please check format file", "", HttpStatus.BAD_REQUEST);
                        }
                    }
                }
            }

            return ResponseUtil.error("Import False", "Import Syllabus False",HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            String result = e.getMessage().toString();
            return ResponseUtil.error(result, "",HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("hasAuthority('syllabus:Full_Access')")
    @DeleteMapping("/syllabus/delete")
    public ResponseEntity<?> changeStatusForUpload(@RequestBody DeleteReplace ids,
        @RequestParam(value = "name") Boolean name,
        @RequestParam(value = "code") Boolean code) {

        return syllabusService.changeStatusforUpload(ids, name, code);
    }


//    @PreAuthorize("hasAuthority('syllabus:Full_Access')")
//    @DeleteMapping("/syllabus/delete")
//    public ResponseEntity<?> changeStatusForUpload(@RequestBody DeleteReplaceSyllabus ids,
//        @RequestParam(value = "name") boolean name,
//        @RequestParam(value = "code") boolean code) {
//        return syllabusService.changeStatusforUpload(ids, name, code);
//    }


}
