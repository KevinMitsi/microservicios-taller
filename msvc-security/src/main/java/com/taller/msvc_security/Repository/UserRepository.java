package com.taller.msvc_security.Repository;


import com.taller.msvc_security.Entities.UserDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<UserDocument, String> {
}
