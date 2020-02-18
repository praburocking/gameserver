
const User = require('../model/user')
const md5=require('md5')
const jwt=require('jsonwebtoken')
const Auth=require('../model/authorization')
const sendmail=require('../../notification/mail/mail')
const scheduler=require('../../scheduler/core/scheduler');
const config =require('../util/config')

  const login=async (data,fromSignup)=>
{
    const user=await User.findOne({email:data.email,passwordHash:md5(data.password)})
        if(user)
        { 
          if( !user.isVerified)
            {
              let token= await issueToken(user,60*15,"verifyUser",true);
              const link=config.SERVER_URL+"/verifyUser?token="+token.key;
              verifyUserMail(user.email, user.name,link);
            }

          if(fromSignup || user.isVerified )
            {
              let token= await issueToken(user,60*60*48,"login")
              return ({token:token.key,username:user.name,id:user._id})
            }
         else 
            {
              return({message:"user not verified sent a verification mail"});
            }
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


const forgotPasswordMail=(to,username,link)=>
{
  const text="Hi "+username+" ! <br> <p> please click on the link to reset your password, the link will be active for only next 15 minutes.</p> <p> please click here <a href='"+link+"'>"+link+"</a></p><br> with regards<br>Prabu.M" 
    const sub="password recovery link"
    scheduler.scheduleJob([to,sub,text],username,{},new Date(),"sendMail",0)
}

const verifyUserMail=(to,username,link)=>
{
  const text="Hi "+username+" ! <br> <p> please click on the link to verify your account, the link will be active for only next 15 minutes.</p> <p> please click here <a href='"+link+"'>"+link+"</a></p><br> with regards<br>Prabu.M" 
    const sub="password recovery link"
    scheduler.scheduleJob([to,sub,text],username,{},new Date(),"sendMail",0)
}

const issueToken=async (user,expiry,action,isDelPrevToken)=>
{
  try{
    const actions=["revivePassword","login","verifyUser"]
  if(actions.includes(action))
  {
  const userForToken = {
    name: user.email,
    id: user._id,
    action:action,
    issueAt: Math.floor(new Date()/1000),
    expiry:expiry
  }
let token=jwt.sign(userForToken,process.env.SECRET)
const splitToken=token.split(".")
const auth =new Auth({key:splitToken[2],payload:splitToken[1]});
token=await auth.save();
token=token.toJSON();
console.log("token1",token);
return token;
  }
  console.log("token2",token);
  }
  catch(exp)
  {
    console.log("exception ",exp);
  }
}

const verifyToken=async (token,action,isRemove)=>
{

  const header=config.SECURITY_HEADER
  let authData= await Auth.find({key:token})
  authData=authData[0];
  if(authData)
  {
  token=header+"."+authData.payload+"."+token
  token=jwt.verify(token,config.SECRET)
  if(token && token.id && token.action===action )
  {
    console.log("verify token ",(token.issueAt+token.expiry) )
    if((token.issueAt+token.expiry) > Math.floor(new Date()/1000))
      {   if(isRemove)
        {
        
        await Auth.deleteOne({_id:authData._id});
        }
      return token;
      }
    else
      {
      return "token-expired"
      }
  }
  else
  {
    return "token-invalid"
  }
}
else
{
  return "token-invalid"
}
}


module.exports={login,invitationMail,issueToken,verifyToken,forgotPasswordMail}