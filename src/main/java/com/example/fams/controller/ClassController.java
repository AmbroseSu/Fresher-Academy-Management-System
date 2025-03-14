package com.example.fams.controller;

import com.example.fams.config.ResponseUtil;
import com.example.fams.dto.CalendarDTO;
import com.example.fams.dto.ClassCalendarDTO;
import com.example.fams.dto.ClassDTO;
import com.example.fams.entities.CalendarClass;
import com.example.fams.entities.enums.WeekDay;
import com.example.fams.services.IClassService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@CrossOrigin
public class ClassController {
    @Autowired
    @Qualifier("ClassService")
    private IClassService classService;

    @PreAuthorize("hasAuthority('class:Full_Access') || hasAuthority('class:View')")
    @GetMapping("/class")
    public ResponseEntity<?> getAllClassByStatusTrue(@RequestParam(defaultValue = "1") int page,
                                                     @RequestParam(defaultValue = "10") int limit) {
        return classService.findAllByStatusTrue(page, limit);
    }

    @PreAuthorize("hasAuthority('class:Full_Access')")
    @GetMapping("/class/hidden")
    public ResponseEntity<?> getAllClasses(@RequestParam(defaultValue = "1") int page,
                                           @RequestParam(defaultValue = "10") int limit) {
        return classService.findAll(page, limit);
    }

    @PreAuthorize("hasAuthority('class:Full_Access') || hasAuthority('class:View')")
    @PostMapping("/class/search/between")
    public ResponseEntity<?> searchBetweenStartDateAndEndDate(@RequestParam(value = "dayStartWeek") Long dayStartWeek,
                                              @RequestParam(value = "dayEndWeek") Long dayEndWeek,
                                              @RequestParam(defaultValue = "1") int page,
                                              @RequestParam(defaultValue = "10") int limit) {
        return classService.searchBetweenStartDateAndEndDate(dayStartWeek, dayEndWeek, page, limit);
    }

    @PreAuthorize("hasAuthority('class:Full_Access') || hasAuthority('class:View')")
    @GetMapping("/class/{id}")
    public ResponseEntity<?> getByClassId(@PathVariable Long id) {
        return classService.findById(id);
    }

    @PreAuthorize("hasAuthority('class:Full_Access') || hasAuthority('class:View')")
    @PostMapping("/class/search")
    public ResponseEntity<?> searchClass(@RequestBody ClassDTO classDTO,
                                         @RequestParam(defaultValue = "1") int page,
                                         @RequestParam(defaultValue = "10") int limit) {
        return classService.searchSortFilter(classDTO, page, limit);
    }

    @PreAuthorize("hasAuthority('class:Full_Access') || hasAuthority('class:View')")
    @PostMapping("/class/search/hidden")
    public ResponseEntity<?> searchClassADMIN(@RequestBody ClassDTO classDTO,
                                              @RequestParam(required = false) String sortById,
                                              @RequestParam(defaultValue = "1") int page,
                                              @RequestParam(defaultValue = "10") int limit) {
        return classService.searchSortFilterADMIN(classDTO, sortById, page, limit);
    }

//    @PreAuthorize("hasAuthority('class:Full_Access') || hasAuthority('class:Create')")
//    @PostMapping("/class")
//    public ResponseEntity<?> createClass(@Valid @RequestBody ClassDTO classDTO) {
//        return classService.save(classDTO);
//    }

    @PreAuthorize("hasAuthority('class:Full_Access') || hasAuthority('class:Create')")
    @PostMapping("/class")
    public ResponseEntity<?> createCaClass(@Valid @RequestBody ClassCalendarDTO classCalendarDTO) {
        return classService.save_withCalendar(classCalendarDTO.getClassDTO(),classCalendarDTO.getWeekDays());
    }

//    @PreAuthorize("hasAuthority('class:Full_Access') || hasAuthority('class:Modify')")
//    @PutMapping("/class/{id}")
//    public ResponseEntity<?> updateClass(@Valid @RequestBody ClassDTO classDTO, @PathVariable(name = "id") Long id) {
//        if(classService.checkExist(id)){
//            classDTO.setId(id);
//            return classService.save(classDTO);
//
//        }
//        return ResponseUtil.error("Not found","Unit not exist", HttpStatus.NOT_FOUND);
//    }

    @PreAuthorize("hasAuthority('class:Full_Access') || hasAuthority('class:Modify')")
    @PutMapping("/class/{id}")
    public ResponseEntity<?> updateClassCalendar(@Valid @RequestBody ClassCalendarDTO classCalendarDTO, @PathVariable(name = "id") Long id) {
        if(classService.checkExist(id)){
            classCalendarDTO.getClassDTO().setId(id);
            return classService.save_withCalendar(classCalendarDTO.getClassDTO(),classCalendarDTO.getWeekDays());

        }
        return ResponseUtil.error("Not found","Unit not exist", HttpStatus.NOT_FOUND);
    }

    @PreAuthorize("hasAuthority('class:Full_Access')")
    @DeleteMapping("/class/{id}")
    public ResponseEntity<?> changeStatus(@PathVariable Long id) {
        return classService.changeStatusClassCalendar(id);
    }
}
