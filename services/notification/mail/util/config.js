require('dotenv').config()

let MONGODB_URL = process.env.MONGODB_URL
let MAIL_ID=process.env.MAIL_ID
let MAIL_PASS=process.env.MAIL_PASS
let ENVIRONMENT=process.env.ENVIRONMENT


module.exports = {
  MONGODB_URL,
 MAIL_ID,
 MAIL_PASS,
 ENVIRONMENT
}
