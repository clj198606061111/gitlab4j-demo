package com.itclj.service;

import com.itclj.config.GitlabConfig;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class GitlabService {


    @Resource
    private GitlabConfig gitlabConfig;


}
