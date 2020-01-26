const mongoose = require('mongoose')
const config=require('../util/config')
const uniqueValidator = require('mongoose-unique-validator')
mongoose.set('useFindAndModify', false)



mongoose.connect(config.MONGODB_URL, {useNewUrlParser: true, useUnifiedTopology: true }).then
    (()=>{console.log("mangodb connected")}).catch((ex)=>{console.log("Exception =>",ex); process.exit(1)})

const schedulerSchema=new mongoose.Schema({
    next_time:{type:Date,required:true},
   is_completed:{type:Boolean,required:true},
   repeat_interval:{type:Object},
   params:{type:Array,required:true},
   user:{type:String,required:true},
   funct:{type:String,required:true},
   remaining_cycle:{type:Number,required:true}
  })

  schedulerSchema.set('toJSON',{
    transform:(document,returnedObject)=>{
      returnedObject.id=returnedObject._id.toString()
      delete returnedObject._id
      delete returnedObject.__v
    }
  })
  module.exports=mongoose.model('Scheduler',schedulerSchema)