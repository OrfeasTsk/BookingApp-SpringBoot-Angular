package tedi.backend.repositories;


import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.RequestParam;
import tedi.backend.model.Department;
import tedi.backend.model.DType;

import java.util.Date;
import java.util.List;

public interface DepartmentRepositoryCustom {

    Department findByName(String name);

    List<Department> find(DType roomType,Float minCost ,Float maxCost, Boolean smokingAllowed,Boolean petsAllowed,Boolean eventsAllowed ,Boolean hasInternet, Boolean hasLivingRoom,Boolean hasAirCondition, Boolean hasHeat, Boolean hasKitchen,
                                   Boolean hasTv, Boolean hasParking, Boolean hasElevator,Date startDate,Date endDate, String location,Integer numberOfPeople, Long hostId);


    List<String> locationFinder(String location);

    Boolean departmentNameExists(String name);

    List<Department> findAvailable();

}

