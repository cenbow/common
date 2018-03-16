package kelly.monitor.service.impl;

import kelly.monitor.common.Application;
import kelly.monitor.common.ApplicationServer;
import kelly.monitor.common.Owner;
import kelly.monitor.common.query.ApplicationQuery;
import kelly.monitor.common.query.ApplicationServerQuery;
import kelly.monitor.common.query.OwnerQuery;
import kelly.monitor.config.JacksonSerializer;
import kelly.monitor.dao.mapper.ApplicationMapper;
import kelly.monitor.dao.mapper.ApplicationServerMapper;
import kelly.monitor.dao.mapper.OwnerMapper;
import kelly.monitor.service.ApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Created by kelly-lee on 2018/3/13.
 */
@Service("applicationService")
public class ApplicationServiceImpl implements ApplicationService {

    @Autowired
    private ApplicationMapper applicationMapper;

    @Autowired
    private ApplicationServerMapper applicationServerMapper;

    @Autowired
    private OwnerMapper ownerMapper;

    @Autowired
    private JacksonSerializer jacksonSerializer;


    public Application getApplicaton(String appCode) {
        ApplicationQuery applicationQuery = ApplicationQuery.builder().appCode(appCode).status(Application.Status.ENABLE).build();
        Application application = applicationMapper.query(applicationQuery);
        application.load(jacksonSerializer);
        if (application != null) {
            application.getOwnerCodes().stream().forEach(ownerCode -> {
                Owner owner = findOwner(ownerCode);
                if (owner != null) {
                    application.getOwners().add(owner);
                }
            });
        }
        return application;
    }

    public Owner findOwner(String code) {
        OwnerQuery ownerQuery = OwnerQuery.builder().code(code).build();
        Optional<Owner> optional = ownerMapper.query(ownerQuery).stream().findAny();
        return optional.isPresent() ? optional.get() : null;
    }

    public List<String> getAppCodes() {
        return applicationMapper.getAppCodes();
    }

    public List<ApplicationServer> getApplicationServers(String appCode) {
        ApplicationServerQuery applicationServerQuery = ApplicationServerQuery.builder().appCode(appCode).build();
        return applicationServerMapper.query(applicationServerQuery);
    }

}
