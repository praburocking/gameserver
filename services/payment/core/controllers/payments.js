const paymentRouter = require('express').Router()
const config=require('../../util/config')
const stripe=require('stripe')(config.PAYMENT_SECRET)
const Payment=require("../../model/payment")




paymentRouter.post('/',async (req,res)=>{
    try{
        const intent = await stripe.paymentIntents.create({
            payment_method_types: ["card"],
            amount: 1099,
            currency: 'inr',
          });
          if(intent.client_secret)
          {
            console.log("intent",intent);
            const payment=new Payment ({
                user_id:res.locals.user_id,
                stripe_client_secret:intent.client_secret,
                stripe_pay_intent_id:intent.id,
                total_amount_paid:intent.amount,
                stripe_payed_date:new Date(),
                is_paid:false,
                plan:"monthly"
            });
            const saved_payment=await payment.save();
            res.status(200).json({...saved_payment.toJSON()}); 
              
          }
}
catch(exp)
{
    res.status(500).json({message:"exception while adding data"}).send()
    console.log("exception while logining ",exp);
}
})


paymentRouter.post('/stripe/webhook',async(req,res)=>
{
    try{
        if (config.STRIPE_WEBHOOK_SECRET) {
            // Retrieve the event by verifying the signature using the raw body and secret.
            let event;
            let signature = req.headers["stripe-signature"];
         try{
                event = stripe.webhooks.constructEvent(
                    req.rawBody,
                    signature,
                   config.STRIPE_WEBHOOK_SECRET
                  );
                 let eventType = event.type;
                  if(eventType==="payment_intent.succeeded")
                  {
                    let data= event.data;
                    console.log("data ",data);

                  let payment_data=await Payment.findOne({stripe_pay_intent_id:data.object.id})
                  console.log("updated cx ",payment_data);
                    if(payment_data)
                    {                        
                        await cx_data.overwrite({...cx_data.toJSON(),is_paid:true});
                        //call your update function.
                        res.status(200).json({message:"processed"});
                       }
                  }

                }
                catch(exp)
                {
                    console.log("exception while validating webhook ",exp);
                    res.status(500).json({message:"exception while verifying"})
                }
            

        }
        

    }
    catch(exp){
        console.log("excpetion occured ",exp);
    }
})

module.exports=paymentRouter