
const User = require('../model/user')
const md5=require('md5')
const jwt=require('jsonwebtoken')
const Auth=require('../model/authorization')
const sendmail=require('../../notification/mail/mail')
const scheduler=require('../../scheduler/core/scheduler');

  const login=async (data)=>
{
    const user=await User.findOne({email:data.email,passwordHash:md5(data.password)})
        if(user)
        {   
            console.log("user =>",user);
            const userForToken = {
                name: user.name,
                id: user._id,
              }
            let token=jwt.sign(userForToken,process.env.SECRET)
            const splitToken=token.split(".")
            console.log("splitToken =>",splitToken);
            const auth =new Auth({key:splitToken[2],payload:splitToken[1]});
           //token = await Auth.add({key:splitToken[2],payload:splitToken[1]}).toJSON();
           token=await auth.save();
           token=token.toJSON();
            console.log(" ",{token:token.key,username:user.name,id:user._id})
           return ({token:token.key,username:user.name,id:user._id})
        }
        else
        {
         return  ({message:"user not found or invalid username/password"})
        }
}
const invitationMail=(to,username,userid)=>
{
    const text="Hi "+username+" ! <br> <p> hope you are doing good and thanks for choosing our service, while you are on your free trail please look around our product and please revert back to me, if you face any difficulty </p><br> with regards<br>Prabu.M" 
    const sub="hi, welcome arkOnline"
   // scheduler.ScheduleJob({},"sendnotification",{seconds:30},new Date(),"sendnotification",12);
    scheduler.scheduleJob([to,sub,text],username,{},new Date(),"sendMail",0)
    //sendmail(to,sub,text);
}


module.exports={login,invitationMail}