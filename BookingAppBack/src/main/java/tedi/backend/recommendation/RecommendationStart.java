package tedi.backend.recommendation;


import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import tedi.backend.model.*;
import tedi.backend.repositories.*;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class RecommendationStart {


    public void InsertDataFromCsv( UserRepository userRepository ,RoleRepository roleRepository ,ReviewRepository reviewRepository,DepartmentRepository departmentRepository,BCryptPasswordEncoder encoder ) {
        String line="";
        int flag = 0;
        float temp=0;
        Long uid = new Long(2);
        Long did = new Long(1);
        Map<Long, Long> depIds = new HashMap<>();
        Map<Long, Long> userIds = new HashMap<>();

        try {
            CSVReader reader = new CSVReader(new FileReader("src/main/resources/listings.csv"));
            String[] nextLine;
            while ((nextLine = reader.readNext()) != null) {
                if (flag == 0) {
                    flag++;
                }
                else if (depIds.containsKey(Long.parseLong(nextLine[0])) == false) {
                    depIds.put(Long.parseLong(nextLine[0]), did);
                    did++;
                    Department d = new Department();
                    User u = new User();
                    Set<Role> roles = new HashSet<Role>();

                    d.setId(Long.parseLong(nextLine[0]));
                    d.setName(nextLine[4]);
                    d.setDescription(nextLine[7]);
                    d.setTransport(nextLine[11]);


                    d.setAddress(nextLine[34]);
                    d.setCity(nextLine[38]);
                    d.setCountry(nextLine[43]);
                    d.setMaxPeople(1);

                    if ((nextLine[44]).equals(""))
                        d.setLatitude(temp);
                    else
                        d.setLatitude(Float.parseFloat(nextLine[44]));

                    if ((nextLine[45]).equals(""))
                        d.setLongitude(temp);
                    else
                        d.setLongitude(Float.parseFloat(nextLine[45]));

                    if (nextLine[45].equals("Private room"))
                        d.setType(DType.privateRoom);
                    else if (nextLine[45].equals("Shared room"))
                        d.setType(DType.publicRoom);
                    else
                        d.setType(DType.apartment);

                    if ((nextLine[50]).equals(""))
                        d.setNumberOfBaths(0);
                    else
                        d.setNumberOfBaths((int) Double.parseDouble(nextLine[50]));

                    if ((nextLine[51]).equals(""))
                        d.setNumberOfBedrooms(0);

                    else
                        d.setNumberOfBedrooms(Integer.parseInt(nextLine[51]));

                    if ((nextLine[52]).equals(""))
                        d.setNumberOfBeds(0);
                    else
                        d.setNumberOfBeds(Integer.parseInt(nextLine[52]));

                    if ((nextLine[55]).equals(""))
                        d.setArea(temp);
                    else
                        d.setArea(Float.parseFloat(nextLine[55]));

                    if ((nextLine[56]).equals(""))
                        d.setCostPerDay(temp);
                    else {
                        if (Character.valueOf(nextLine[56].charAt(0)).compareTo(Character.valueOf('$')) == 0) {
                            line = nextLine[56].substring(1);
                            d.setCostPerDay(Float.parseFloat(line));
                        } else {
                            d.setCostPerDay(Float.parseFloat(nextLine[56]));
                        }
                    }

                    d.setCostPerPerson(temp);

                    if ((nextLine[63]).equals(""))
                        d.setMinBookingDays(0);
                    else
                        d.setMinBookingDays(Integer.parseInt(nextLine[63]));

                    if ((nextLine[72]).equals(""))
                        d.setNumberOfReviews(0);
                    else
                        d.setNumberOfReviews(Integer.parseInt(nextLine[72]));


                    if (userIds.containsKey(Long.parseLong(nextLine[16])) == true ) {
                        u = userRepository.findById(userIds.get(Long.parseLong(nextLine[16]))).get();
                    } else {
                        userIds.put(Long.parseLong(nextLine[16]), uid);
                        u.setId(Long.parseLong(nextLine[16]));
                        u.setUsername("user" + nextLine[16]);
                        u.setLastName("bot");
                        u.setFirstName(nextLine[18]);
                        u.setEmail("user" + nextLine[16] + "@gmail.com");
                        u.setPassword(encoder.encode("bot"));
                        roles.add(roleRepository.findByName(RoleNames.ROLE_HOST));
                        u.setRoles(roles);
                        uid++;
                    }
                    d.setHost(u);
                    departmentRepository.save(d);
                }
            }
            CSVReader reader1 = new CSVReader(new FileReader("src/main/resources/reviews.csv"));
            flag = 0;


            while ((nextLine = reader1.readNext()) != null && flag <=300)
            {
                if (flag == 0) {
                    flag++;
                } else {
                    Department d;
                    User u;
                    Review r = new Review();

                    r.setId(Long.parseLong(nextLine[1]));

                    if(depIds.containsKey(Long.parseLong(nextLine[0])) == true ){
                        d =departmentRepository.findById(depIds.get(Long.parseLong(nextLine[0]))).get();
                    }
                    else
                    {
                        d= new Department();
                        User u1 = new User();
                        Set<Role> roles1 = new HashSet<Role>();

                        d.setId(Long.parseLong(nextLine[0]));
                        d.setName("bot_dep");
                        d.setDescription("bot_dep");
                        d.setTransport("bot_dep");


                        d.setAddress("bot_dep");
                        d.setCity("bot_dep");
                        d.setCountry("bot_dep");
                        d.setMaxPeople(1);
                        d.setLatitude(temp);
                        d.setLongitude(temp);
                        d.setType(DType.apartment);
                        d.setNumberOfBaths(0);
                        d.setNumberOfBedrooms(0);
                        d.setNumberOfBeds(0);
                        d.setArea(temp);
                        d.setCostPerDay(temp);
                        d.setCostPerPerson(temp);
                        d.setMinBookingDays(0);
                        d.setNumberOfReviews(0);
                    }


                    if(userIds.containsKey(Long.parseLong(nextLine[3])) == true){
                        boolean flg = false;
                        u = userRepository.findById(userIds.get(Long.parseLong(nextLine[3]))).get();
                        Set<Role> roles = new HashSet<Role>();

                        for(Role role : u.getRoles()){
                            if(role.getName().equals(RoleNames.ROLE_TENANT) )
                                flg = true;
                            roles.add(role);
                        }

                        if(flg == false)
                            roles.add(roleRepository.findByName(RoleNames.ROLE_TENANT));
                        u.setRoles(roles);
                    }
                    else {
                        userIds.put(Long.parseLong(nextLine[3]), uid);
                        u = new User();
                        Set<Role> roles = new HashSet<Role>();
                        u.setId(Long.parseLong(nextLine[3]));
                        u.setUsername("user" + nextLine[3]);
                        u.setLastName("bot");
                        u.setFirstName(nextLine[4]);
                        u.setEmail("bot" + nextLine[3] + "@gmail.com");
                        u.setPassword(encoder.encode("bot"));
                        roles.add(roleRepository.findByName(RoleNames.ROLE_TENANT));
                        u.setRoles(roles);
                        uid++;
                    }
                    r.setFromUser(u);
                    r.setForDepartment(d);
                    r.setText(nextLine[5]);

                    r.setStars(NLP.findSentiment(nextLine[5]) + 1);

                    reviewRepository.save(r);
                    flag++;
                }
            }


        } catch (FileNotFoundException er) {
            er.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (CsvValidationException e) {
            e.printStackTrace();
        }

    }





    public void recInit(UserRepository userRepository , RoleRepository roleRepository, DepartmentRepository departmentRepository, ReviewRepository reviewRepository , BookingRepository bookingRepository,InteractionRepository interactionRepository ,BCryptPasswordEncoder encoder) {

        /*NLP.init();
        if(userRepository.findAll().size() == 1)
            InsertDataFromCsv(userRepository,roleRepository,reviewRepository,departmentRepository,encoder);*/

        List<User> userList = userRepository.findByRole(RoleNames.ROLE_TENANT);
        List<Department> departmentList = departmentRepository.findAll();

        for(int u = 0; u < userList.size() ; u++) {
            userList.get(u).setRecommendedDepartments(null);
            userRepository.save(userList.get(u));
        }
        for(int d = 0; d < departmentList.size() ; d++){
            departmentList.get(d).setRecommendedTo(null);
            departmentRepository.save(departmentList.get(d));
        }

        userList = userRepository.findByRole(RoleNames.ROLE_TENANT);
        departmentList = departmentRepository.findAll();

        //System.out.println(userList.size());
        //System.out.println(departmentList.size());


        if(userList.size() > 0 && departmentList.size() > 0) {
            double[][] reviews = new double[userList.size()][departmentList.size()];


            for (int u = 0; u < userList.size(); u++) {
                int count = 0;
                double val = 0;
                for (int d = 0; d < departmentList.size(); d++) {
                    reviews[u][d] = reviewRepository.getRating(userList.get(u).getId(), departmentList.get(d).getId());
                    if (reviews[u][d] != -1) {
                        val += reviews[u][d];
                        count++;
                    }
                    if (reviews[u][d] == -1 && bookingRepository.hasBooked(departmentList.get(d).getId(), userList.get(u).getId())){
                        reviews[u][d] = -2;
                    }
                }

                    for(int d = 0 ; d < departmentList.size(); d++){
                        if (reviews[u][d] == -2 && count != 0)
                            reviews[u][d] = val / count;
                        else if(count == 0) {
                            Interaction interaction = interactionRepository.getInteraction(departmentList.get(d).getId(), userList.get(u).getId());
                            if (interaction != null)
                                reviews[u][d] = (interaction.getCounter() * 0.05 + 3 < 5) ? interaction.getCounter() * 0.1 + 3 : 5;
                            else
                                reviews[u][d] = -1;
                        }
                    }

            }


            Recommendation recommendation = new Recommendation();
            double[][] results = recommendation.matrix_factorization(reviews, 2, 0.0002, 0.0);
            //recommendation.print(reviews);
            //recommendation.print(results);

            for (int u = 0; u < userList.size(); u++) {
                Set<Department> departments = new HashSet<Department>();
                List<Pair> pairs = new ArrayList<Pair>();
                for (int d = 0; d < departmentList.size(); d++) {
                    if (reviews[u][d] == -1)
                        pairs.add(new Pair(d, results[u][d]));
                }
                pairs.sort((Pair p1, Pair p2) -> Double.compare(p2.value, p1.value));
                if (pairs.size() > 0) {
                    int topk = (pairs.size() < 5) ? pairs.size() : 5;
                    for (int i = 0; i < topk; i++)
                        departments.add(departmentList.get(pairs.get(i).index));
                    userList.get(u).setRecommendedDepartments(departments);
                }
                if( pairs.size() == departmentList.size())
                    userList.get(u).setRecommendedDepartments(null);
            }

            for (int u = 0; u < userList.size(); u++)
                userRepository.save(userList.get(u));

        }
        
    }


}

