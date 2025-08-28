package com.taller.msvc_security.Entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "user_documents")
public class UserDocument {
    @Id
    private String id;


}
