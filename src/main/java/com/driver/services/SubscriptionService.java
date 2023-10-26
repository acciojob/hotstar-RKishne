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

        User user= subscriptionRepository.findById(subscriptionEntryDto.getUserId()).get().getUser();
        int priceOfSubscription=0;
        if(subscription.getSubscriptionType().equals(SubscriptionType.BASIC)){
            priceOfSubscription=(500+ (200*subscriptionEntryDto.getNoOfScreensRequired()));
        }
        else if(subscription.getSubscriptionType().equals(PRO)){
            priceOfSubscription=(800 + (250 *subscriptionEntryDto.getNoOfScreensRequired()));
        }
        else if(subscription.getSubscriptionType().equals(ELITE)){
            priceOfSubscription=(1000 + (350 *subscriptionEntryDto.getNoOfScreensRequired()));
        }
        subscription.setTotalAmountPaid(priceOfSubscription);
        user.setSubscription(subscription);

        return subscription.getTotalAmountPaid();
    }

    public Integer upgradeSubscription(Integer userId)throws Exception{

        //If you are already at an ElITE subscription : then throw Exception ("Already the best Subscription")
        //In all other cases just try to upgrade the subscription and tell the difference of price that user has to pay
        //update the subscription in the repository
        User user=userRepository.findById(userId).get();

        String subscriptionType = user.getSubscription().getSubscriptionType().toString();

        int amountToBePaidBeforeUpdation=user.getSubscription().getTotalAmountPaid();

        int amountToBePaidAfterUpdation=0;

        if(subscriptionType.equals(ELITE)){
            throw new Exception("Already the best Subscription");
        }
        else if(subscriptionType.equals(PRO)){
            user.getSubscription().setSubscriptionType(ELITE);
            amountToBePaidAfterUpdation=(200 + 100*user.getSubscription().getNoOfScreensSubscribed())+amountToBePaidBeforeUpdation;
        }
        else if(subscriptionType.equals(BASIC)){
            user.getSubscription().setSubscriptionType(PRO);
            amountToBePaidAfterUpdation=(300 + 50*user.getSubscription().getNoOfScreensSubscribed())+amountToBePaidBeforeUpdation;
        }
        Subscription subscription=user.getSubscription();
        subscription.setTotalAmountPaid(amountToBePaidAfterUpdation);
        user.setSubscription(subscription);
        subscriptionRepository.save(subscription);

        return amountToBePaidAfterUpdation-amountToBePaidBeforeUpdation;
    }

    public Integer calculateTotalRevenueOfHotstar(){

        //We need to find out total Revenue of hotstar : from all the subscriptions combined
        //Hint is to use findAll function from the SubscriptionDb
        List<Subscription> subscriptionList=subscriptionRepository.findAll();

        int totalRevenue=0;

        for(Subscription subscription:subscriptionList){
            totalRevenue+=subscription.getTotalAmountPaid();
        }
        return totalRevenue;
    }

}
