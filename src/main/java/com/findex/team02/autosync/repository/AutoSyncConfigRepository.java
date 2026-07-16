package com.findex.team02.autosync.repository;

import com.findex.team02.autosync.entity.AutoSyncConfig;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AutoSyncConfigRepository extends JpaRepository<AutoSyncConfig, Long>, AutoSyncConfigRepositoryCustom {

    List<AutoSyncConfig> findAllByEnabledTrue();

}
