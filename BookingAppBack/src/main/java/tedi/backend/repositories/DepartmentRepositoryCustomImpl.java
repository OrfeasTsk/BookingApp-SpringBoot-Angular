package tedi.backend.repositories;


import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import tedi.backend.model.DType;
import tedi.backend.model.Department;


import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.Date;
import java.util.List;

@Repository
@Transactional(readOnly = true)
public class DepartmentRepositoryCustomImpl  implements DepartmentRepositoryCustom{


    @PersistenceContext
    EntityManager entityManager;

    @Override
    public List<Department> find(DType roomType, Float minCost , Float maxCost, Boolean smokingAllowed, Boolean petsAllowed, Boolean eventsAllowed , Boolean hasInternet, Boolean hasLivingRoom, Boolean hasAirCondition, Boolean hasHeat, Boolean hasKitchen,
                                          Boolean hasTv, Boolean hasParking, Boolean hasElevator, Date startDate, Date endDate, String location, Integer numberOfPeople, Long hostId){



        int diff = (int)(endDate.getTime()-startDate.getTime())/(1000*60*60*24);


        String queryStr = "select d from Department d where (d.country = ?1 or d.city = ?1) and  d.startDate <= ?2 and d.endDate >= ?3 and d.maxPeople >= ?4 and d.minBookingDays <= ?5 and d.id not in " +
                "(select d.id from Booking b , Department d where b.department.id = d.id and ((b.startDate <= ?2 and b.endDate >= ?2) or (b.endDate >= ?3 and b.startDate <= ?3) or (?2 < b.startDate and ?3 > b.endDate)))";
        Integer paramNum = 6;


        if(hostId != null){
            queryStr += " and ";
            queryStr += " d.host.id != ?" + paramNum.toString();
            paramNum++;

        }

        if (roomType != null) {
            queryStr += " and ";
            queryStr += "d.type = ?" + paramNum.toString();
            paramNum++;
        }

        if (minCost != null) {
            queryStr += " and ";
            queryStr += "d.costPerDay + ?4 * d.costPerPerson >= ?" + paramNum.toString();
            paramNum++;
        }

        if (maxCost != null) {
            queryStr += " and ";
            queryStr += "d.costPerDay + ?4 * d.costPerPerson <= ?" + paramNum.toString();
            paramNum++;
        }

        if (hasInternet != null) {
            queryStr += " and ";
            queryStr += "d.hasInternet = ?" + paramNum.toString();
            paramNum++;
        }
        if (hasLivingRoom != null) {
            queryStr += " and ";
            queryStr += "d.hasLivingRoom = ?" + paramNum.toString();
            paramNum++;
        }

        if (hasAirCondition != null) {
            queryStr += " and ";
            queryStr += "d.hasAirCondition = ?" + paramNum.toString();
            paramNum++;
        }

        if (hasHeat != null) {
            queryStr += " and ";
            queryStr += "d.hasHeat = ?" + paramNum.toString();
            paramNum++;
        }

        if (hasKitchen != null) {
            queryStr += " and ";
            queryStr += "d.hasKitchen = ?" + paramNum.toString();
            paramNum++;
        }

        if (hasTv != null) {
            queryStr += " and ";
            queryStr += "d.hasTv = ?" + paramNum.toString();
            paramNum++;
        }

        if (hasParking != null) {
            queryStr += " and ";
            queryStr += "d.hasParking = ?" + paramNum.toString();
            paramNum++;
        }

        if (hasElevator != null) {
            queryStr += " and ";
            queryStr += "d.hasElevator = ?" + paramNum.toString();
            paramNum++;
        }
        if (smokingAllowed != null) {
            queryStr += " and ";
            queryStr += "d.smokingAllowed = ?" + paramNum.toString();
            paramNum++;
        }
        if (petsAllowed != null) {
            queryStr += " and ";
            queryStr += "d.petsAllowed = ?" + paramNum.toString();
            paramNum++;
        }
        if (eventsAllowed != null) {
            queryStr += " and ";
            queryStr += "d.eventsAllowed = ?" + paramNum.toString();
            paramNum++;
        }


        Query query = entityManager.createQuery(queryStr);
        paramNum = 1;

        query.setParameter(paramNum, location);
        paramNum++;
        query.setParameter(paramNum, startDate);
        paramNum++;
        query.setParameter(paramNum,endDate);
        paramNum++;
        query.setParameter(paramNum,numberOfPeople);
        paramNum++;
        query.setParameter(paramNum,diff);
        paramNum++;


        if(hostId != null){
            query.setParameter(paramNum, hostId);
            paramNum++;

        }

        if (roomType != null){
            query.setParameter(paramNum, roomType);
            paramNum++;
        }
        if (minCost != null) {
            query.setParameter(paramNum, minCost);
            paramNum++;
        }
        if (maxCost != null) {
            query.setParameter(paramNum, maxCost);
            paramNum++;
        }
        if (hasInternet != null){
            query.setParameter(paramNum, hasInternet);
            paramNum++;
        }
        if (hasLivingRoom != null){
            query.setParameter(paramNum, hasLivingRoom);
            paramNum++;
        }
        if (hasAirCondition != null){
            query.setParameter(paramNum, hasAirCondition);
            paramNum++;
        }
        if (hasHeat != null) {
            query.setParameter(paramNum, hasHeat);
            paramNum++;
        }
        if (hasKitchen != null){
            query.setParameter(paramNum, hasKitchen);
            paramNum++;
        }
        if (hasTv != null) {
            query.setParameter(paramNum, hasTv);
            paramNum++;
        }
        if (hasParking != null){
            query.setParameter(paramNum, hasParking);
            paramNum++;
        }

        if (hasElevator != null) {
            query.setParameter(paramNum, hasElevator);
            paramNum++;
        }
        if (smokingAllowed != null) {
            query.setParameter(paramNum, smokingAllowed);
            paramNum++;
        }
        if (petsAllowed != null) {
            query.setParameter(paramNum, petsAllowed);
            paramNum++;
        }
        if (eventsAllowed != null) {
            query.setParameter(paramNum, eventsAllowed);
            paramNum++;
        }


        return query.getResultList();
    }



    @Override
    public Department findByName(String name) {
        Department department = null;
        Query query = entityManager.createQuery("SELECT d FROM Department d WHERE d.name = ?1");
        query.setParameter(1, name);
        List<Department> departments = query.getResultList();
        if (departments != null && departments.size() > 0)
            department = departments.get(0);
        return department;
    }

    @Override
    public Boolean departmentNameExists(String name) {
        Department department = null;
        Query query = entityManager.createQuery("SELECT d FROM Department d WHERE d.name = ?1");
        query.setParameter(1, name);
        List<Department> departments = query.getResultList();
        if (departments != null && departments.size() > 0)
            return true;
        else
            return false;
    }

    @Override
    public List<String> locationFinder(String location) {

        Query query = entityManager.createQuery("SELECT DISTINCT d.city  FROM Department d WHERE (d.city  LIKE CONCAT(?1,'%') )");
        query.setParameter(1, location);
        List<String> all= query.getResultList();
        query = entityManager.createQuery("SELECT DISTINCT d.country FROM Department d WHERE (d.country LIKE CONCAT(?1,'%'))");
        query.setParameter(1, location);
        all.addAll(query.getResultList());
        return all;
    }


    @Override
    public List<Department> findAvailable(){
        Date currentDate = new Date();
        Query query = entityManager.createQuery("SELECT d FROM Department d WHERE d.endDate >= ?1");
        query.setParameter(1, currentDate);
        return query.getResultList();
    }





}
