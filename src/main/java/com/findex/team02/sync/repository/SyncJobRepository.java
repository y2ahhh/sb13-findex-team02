package com.findex.team02.sync.repository;

import com.findex.team02.sync.entity.SyncJob;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SyncJobRepository extends JpaRepository<SyncJob, Long>, SyncJobRepositoryCustom {
}
