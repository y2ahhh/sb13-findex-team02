package com.findex.team02.indexdata.repository;

import com.findex.team02.indexdata.entity.IndexData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IndexDataRepository extends JpaRepository<IndexData, Long>,
    IndexDataRepositoryCustom {

}
