require('dotenv').config()

let MONGODB_URL = process.env.MONGODB_URL
let MAIL_ID=process.env.MAIL_ID
let MAIL_PASS=process.env.MAIL_PASS
let ENVIRONMENT=process.env.ENVIRONMENT
let MAIL_PORT=process.env.MAIL_PORT
let MAIL_DOMAIN=process.env.MAIL_DOMAIN


module.exports = {
  MONGODB_URL,
 MAIL_ID,
 MAIL_PASS,
 ENVIRONMENT,
 MAIL_PORT,
 MAIL_DOMAIN

}
