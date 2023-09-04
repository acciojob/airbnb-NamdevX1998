package com.driver.controllers;

import com.driver.model.Booking;
import com.driver.model.Facility;
import com.driver.model.Hotel;
import com.driver.model.User;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class HotelManagementRepository {
    Map<String,Hotel> hoteldb=new HashMap<>();    //hotel_name && hotel(object)
    Map<Integer,User>userdb=new HashMap<>();      // Aadhaar_card && user(object)
    Map<String,Integer>bookingdb=new HashMap<>();     // bookingId && Aadhaar_card
   // Map<Integer,List<String>>user_hotelsdb=new HashMap<>();    // Aadhaar_card  && List of hotel_name

    Map<Integer,Integer> NoOfbookingsByUser =new HashMap<>();    // Aadhaar_card  && no.of bookings

    public String addHotel(Hotel hotel) {
        if(hoteldb.containsKey(hotel.getHotelName()))   //already present
            return "FAILURE";
        String name=hotel.getHotelName();
        hoteldb.put(name,hotel);
        return "SUCCESS";
    }

    public int addUser(User user) {
        userdb.put(user.getaadharCardNo(),user);
        return user.getaadharCardNo();
    }

    public String getHotelWithMostFacilities() {
        if(hoteldb.size()==0)
            return "";
        int maxi=Integer.MIN_VALUE;
        for(String str:hoteldb.keySet()){
            List<Facility> Facilities=hoteldb.get(str).getFacilities();
            if(maxi<Facilities.size()){
                maxi=Facilities.size();
            }
        }
        if(maxi==0)
            return "";
        List<String>name=new ArrayList<>();
        for(String str:hoteldb.keySet()) {
            List<Facility> Facilities = hoteldb.get(str).getFacilities();
            if (maxi == Facilities.size()) {
                name.add(str);
            }
        }

        if(name.size()==1)
            return name.get(1);
        String ans=name.get(1);
        for(String str:name){
            int temp=ans.compareTo(str);
            if(temp<=0)
                ans=str;
        }
        return ans;
    }

    public int bookARoom(Booking booking) {
        String bookingid= String.valueOf(UUID.randomUUID());
        booking.setBookingId(bookingid);
        int booking_Aadhaar_card=booking.getBookingAadharCard();
        String booking_person_name=booking.getBookingPersonName();
        String hotel_name= booking.getHotelName();
        int req_rooms=booking.getNoOfRooms();

//        if(hoteldb.containsKey(hotel_name)==false || userdb.containsKey(booking_Aadhaar_card))
//            return -1;

        if(hoteldb.containsKey(hotel_name)==false)
            return -1;
        int avail_rooms=hoteldb.get(hotel_name).getAvailableRooms();
        if(req_rooms>avail_rooms)
            return -1;


        if(NoOfbookingsByUser.containsKey(booking_Aadhaar_card)){
            int alreadybookedrooms= NoOfbookingsByUser.get(booking_Aadhaar_card);
            NoOfbookingsByUser.put(booking_Aadhaar_card,alreadybookedrooms+req_rooms);
        }else{
            NoOfbookingsByUser.put(booking_Aadhaar_card,req_rooms);
        }


//        if(user_hotelsdb.containsKey(booking_Aadhaar_card)){
//            List<String>hotels=user_hotelsdb.get(booking_Aadhaar_card);
//            hotels.add(hotel_name);
//            user_hotelsdb.put(booking_Aadhaar_card,hotels);
//        }else{
//            List<String>hotels=new ArrayList<>();
//            hotels.add(hotel_name);
//            user_hotelsdb.put(booking_Aadhaar_card,hotels);
//        }

        return (hoteldb.get(hotel_name).getPricePerNight())*req_rooms;
    }

    public int getBookings(Integer aadharCard) {
        if(NoOfbookingsByUser.containsKey(aadharCard)==false)
            return 0;
        return NoOfbookingsByUser.get(aadharCard);
    }

    public Hotel updateFacilities(List<Facility> newFacilities, String hotelName) {
        if(hoteldb.containsKey(hotelName)==false)
            return null;
        Hotel hotel=hoteldb.get(hotelName);
        List<Facility>facilities=hotel.getFacilities();
        for(Facility facility:newFacilities){
            if(facilities.contains(facility)==false)
            facilities.add(facility);
        }
        return hotel;
    }
}
