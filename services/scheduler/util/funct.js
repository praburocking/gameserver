//what ever the function you want to schedule please import it here and then use it in schedule job as funct.{function_name};
//funct name and its key should be same and unique across the list.
//const sendmail=require('../../notification/mail/mail')
const sendnotification=require('../../notification/slack/slack')
const test=(a,b,c)=>
{
    console.log("HI iam test and I have a few parameters",a,b,c);

}
const funct={
test:test,
sendnotification:sendnotification
}

module.exports=funct;