const mongoose = require('mongoose')
const config=require('../utils/config')
const uniqueValidator = require('mongoose-unique-validator')
mongoose.set('useFindAndModify', false)


console.log("config =>",config);
let url=config.MONGODB_URL;
console.log("mongodb url from env file=> ",url);


mongoose.connect(url, { useNewUrlParser: true, useUnifiedTopology: true }).then
(()=>{console.log("mangodb connected")}).catch((ex)=>{console.log("Exception =>",ex)})



const fileSchema=new mongoose.Schema({
    name:{type:String,required:true},
    user_id:{type:String,required:true,minlength:20},
    private_key:{type:String,required:true,minlength:8},
    format:{type:String,required:true},
    size:{type:Number,required:true},
    encoding:{type:String},
    md5:{type:String},
    truncated:{type:Boolean}
  })


  fileSchema.plugin(uniqueValidator)

  fileSchema.set('toJSON',{
    transform:(document,returnedObject)=>{
      returnedObject.id=returnedObject._id.toString()
      delete returnedObject._id
      delete returnedObject.__v
      delete returnedObject.private_key
    }

  })


  module.exports=mongoose.model('file',fileSchema)