package com.example.fams.services;

import com.example.fams.entities.Class;
import com.example.fams.repository.ClassRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ClassService {
  @Autowired
  private ClassRepository classRepository;

  public void save(Class classes) {
    classRepository.save(classes);
  }
  public List<Class> findAll(){
    return classRepository.findAll();
  }
  public void saveAll(List<Class> classes) {
    classRepository.saveAll(classes);
  }

  public void delete(String code){
    if(classRepository.findByCode(code)==null){
      return;
    }
    classRepository.delete(classRepository.findByCode(code));
  }
  public void deleteId(Integer code){
    classRepository.deleteById(code);
  }

  public void deleteAll(){
    classRepository.deleteAll();
  }

}
