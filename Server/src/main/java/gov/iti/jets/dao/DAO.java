package gov.iti.jets.dao;

import java.util.List;

public interface DAO<T>{



    //////// CRUD --> create = insert,  read = get;
    T create(T t);


    T read(T t);


    T update(T t);


    void delete(T t);

    
    List<T> findAll();


}
