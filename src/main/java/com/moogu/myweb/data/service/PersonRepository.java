package com.moogu.myweb.data.service;

import com.moogu.myweb.data.entity.Person;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonRepository extends JpaRepository<Person, Integer> {

}