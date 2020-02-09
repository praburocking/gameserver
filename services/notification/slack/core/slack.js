const config=require('../util/config')
const axios=require('axios')


const notificationType={
  warning:"warning",
  info:"info",
  exception:"exception"
}




const sendSlackMsg= async (data)=>
{


  const userAccountNotification = {
    'username': 'Error notifier', // This will appear as user name who posts the message
    'text': 'User failed to login 3 times. Account locked for 15 minutes.', // text
    'icon_emoji': ':bangbang:', // User icon, you can also use custom icons here
    'attachments': [{ // this defines the attachment block, allows for better layout usage
      'color': '#eed140', // color of the attachments sidebar.
      'fields': [ // actual fields
        {
          'title': 'Environment', // Custom field
          'value': 'Production', // Custom value
          'short': true // long fields will be full width
        },
        
      ]
    }]
  };

if(data.userid!==undefined && data.userid!==null && data.userid!=="")
{
userAccountNotification.attachments[0].fields.push({
  'title': 'User ID',
  'value': data.userid,
  'short': true
})
}
if(data.message)
{
  userAccountNotification.text=data.message
}
if(data.type)
{
  userAccountNotification.username=data.type
}
await sendnotification(userAccountNotification);
}


const sendnotification=async (data)=>
{

   const res=await axios.post(config.SLACK_URL,data);
  
  
   console.log("slack res",res)
}

module.exports=sendSlackMsg