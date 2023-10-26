package com.driver.services;


import com.driver.EntryDto.SubscriptionEntryDto;
import com.driver.model.Subscription;
import com.driver.model.SubscriptionType;
import com.driver.model.User;
import com.driver.repository.SubscriptionRepository;
import com.driver.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

import static com.driver.model.SubscriptionType.*;

@Service
public class SubscriptionService {

    @Autowired
    SubscriptionRepository subscriptionRepository;

    @Autowired
    UserRepository userRepository;

    public Integer buySubscription(SubscriptionEntryDto subscriptionEntryDto){

        //Save The subscription Object into the Db and return the total Amount that user has to pay
        Subscription subscription=new Subscription();
        subscription.setSubscriptionType(subscriptionEntryDto.getSubscriptionType());
        subscription.setNoOfScreensSubscribed(subscriptionEntryDto.getNoOfScreensRequired());
        subscription.setStartSubscriptionDate(new Date());
        SubscriptionType subscriptionType = subscription.getSubscriptionType();
        int noOfScreen = subscription.getNoOfScreensSubscribed();

        User user= subscriptionRepository.findById(subscriptionEntryDto.getUserId()).get().getUser();

        int priceOfSubscription=0;

        if(subscriptionType.equals(SubscriptionType.BASIC)){
            priceOfSubscription = 500 + (200 *noOfScreen);
        }else if(subscriptionType.equals(SubscriptionType.PRO)){
            priceOfSubscription = 800 + (250 * noOfScreen);
        }else{
            priceOfSubscription = 1000 + (350 * noOfScreen);
        }
        subscription.setTotalAmountPaid(priceOfSubscription);
        subscription.setUser(user);
        user.setSubscription(subscription);

        return subscription.getTotalAmountPaid();
    }

    public Integer upgradeSubscription(Integer userId)throws Exception{

        //If you are already at an ElITE subscription : then throw Exception ("Already the best Subscription")
        //In all other cases just try to upgrade the subscription and tell the difference of price that user has to pay
        //update the subscription in the repository
        User user=userRepository.findById(userId).get();
        if(user.getSubscription().getSubscriptionType().toString().equals("ELITE")){
            throw new Exception("Already the best Subscription");
        }

        Subscription subscription=user.getSubscription();
        Integer totalAmountPaidBeforeUpgradation=subscription.getTotalAmountPaid();
        Integer totalAmountPaidAfterUpgradation;
        if(subscription.getSubscriptionType().equals(SubscriptionType.BASIC)){
            subscription.setSubscriptionType(SubscriptionType.PRO);
            totalAmountPaidAfterUpgradation =totalAmountPaidBeforeUpgradation+300+(50*subscription.getNoOfScreensSubscribed());
        }else {
            subscription.setSubscriptionType(SubscriptionType.ELITE);
            totalAmountPaidAfterUpgradation=totalAmountPaidBeforeUpgradation+200+(100*subscription.getNoOfScreensSubscribed());
        }

        subscription.setTotalAmountPaid(totalAmountPaidAfterUpgradation);
        user.setSubscription(subscription);
        subscriptionRepository.save(subscription);

        return totalAmountPaidAfterUpgradation-totalAmountPaidBeforeUpgradation;
    }

    public Integer calculateTotalRevenueOfHotstar(){

        //We need to find out total Revenue of hotstar : from all the subscriptions combined
        //Hint is to use findAll function from the SubscriptionDb
        List<Subscription> subscriptionList=subscriptionRepository.findAll();

        Integer totalRevenue=0;

        for(Subscription subscription:subscriptionList){
            totalRevenue+=subscription.getTotalAmountPaid();
        }
        return totalRevenue;
    }

}
