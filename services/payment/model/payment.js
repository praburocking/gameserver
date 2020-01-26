const mongoose = require('mongoose')
const config=require('../util/config')
const uniqueValidator = require('mongoose-unique-validator')
mongoose.set('useFindAndModify', false)


console.log("config =>",config);
let url=config.MONGODB_URL;
console.log("mongodb url from env file=> ",url);


mongoose.connect(url, { useNewUrlParser: true, useUnifiedTopology: true }).then
(()=>{console.log("mangodb connected")}).catch((ex)=>{console.log("Exception =>",ex)})



const paymentSchema=new mongoose.Schema({
    
    user_id:{type:String,required:true,minlength:20},
    stripe_client_secret:{type:String,unique:true},
    stripe_pay_intent_id:{type:String,unique:true},
    total_amount_paid:{type:Number},
    stripe_payed_date:{type:Date},
    is_paid:{type:String},
    plan:{type:String},
  })
 

  paymentSchema.plugin(uniqueValidator)

  paymentSchema.set('toJSON',{
    transform:(document,returnedObject)=>{
      returnedObject.id=returnedObject._id.toString()
      delete returnedObject._id
      delete returnedObject.__v
    }

  })


  module.exports=mongoose.model('Payment',paymentSchema)