package com.example.fams.services;

import com.example.fams.entities.Content;
import com.example.fams.repository.ContentRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ContentService {

  @Autowired
  private ContentRepository contentRepository;


  public void save(Content content) {
    contentRepository.save(content);
  }
  public List<Content> findAll(){
    return contentRepository.findAll();
  }
  public void saveAll(List<Content> contents) {
    contentRepository.saveAll(contents);
  }

//  public void delete(String code){
//    if(contentRepository.findByCode(code)==null){
//      return;
//    }
//    classRepository.delete(classRepository.findByCode(code));
//  }
  public void deleteId(Long id){
    contentRepository.deleteById(id);
  }

  public void deleteAll(){
    contentRepository.deleteAll();
  }


}
