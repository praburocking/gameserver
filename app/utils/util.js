const multer=require('multer')
const File=require('../models/uploads')
const { Transform } = require('stream');
var fs = require('fs');
const zlib = require('zlib');
const crypto=require('crypto')
const stream = require('stream')
const EventEmitter = require('events');


var Readable = require('stream').Readable; 


var storage = multer.diskStorage({
    destination: function (req, file, cb) {
    cb(null, 'uploads')
  },
  filename: function (req, file, cb) {
    cb(null, Date.now() + '-' +file.originalname )
  }
})

var upload = multer({ storage: storage }).single('file')

const addFile=async (name,format,user_id,private_key,size,encoding,md5,truncated)=>
{
 
    try{
        const file=new File({name:name,format:format,user_id:user_id,private_key:private_key,size:size,encoding:encoding,md5:md5,truncated:truncated})
        const savedFile=await file.save()
        return savedFile

    }
    catch(exp)
    {
        console.log("exception while adding file ",exp);
    }
}

const streamToBuffer=()=>
{
  var converter = new stream.Writable();
converter.data = []; 
converter._write = function (chunk) {
  this.data.push(chunk);
};
converter.on('end', function() { 
  var b = Buffer.concat(this.data); 
  return b;
});
}

const getFiles=async(user_id)=>
{
let files=await File.find({user_id:user_id});
files=files.map(file=>file.toJSON())
return files;
}

const generatePassword = (
  passwordLength = 8,
  useUpperCase = true,
  useNumbers = true,
  useSpecialChars = true,
) => {
  const chars = 'abcdefghijklmnopqrstuvwxyz'
  const numberChars = '0123456789'
  const specialChars = '!"£$%^&*()'

  const usableChars = chars
   + (useUpperCase ? chars.toUpperCase() : '')
   + (useNumbers ? numberChars : '')
   + (useSpecialChars ? specialChars : '')

  let generatedPassword = ''

  for(i = 0; i <= passwordLength; i++) {
    generatedPassword += usableChars[Math.floor(Math.random() * (usableChars.length))]
  }

  return generatedPassword
}

function bufferToStream(buffer) { 
  var stream = new Readable();
  stream.push(buffer);
  stream.push(null);

  return stream;
}
function getCipherKey(password) {
    return crypto.createHash('sha256').update(password).digest();
  }


class AppendInitVect extends Transform {
  constructor(initVect, opts) {
    super(opts);
    this.initVect = initVect;
    this.appended = false;
  }

  _transform(chunk, encoding, cb) {
    if (!this.appended) {
      this.push(this.initVect);
      this.appended = true;
    }
    this.push(chunk);
    cb();
  }
}

const encAndaddFile=async (upload,key,user_id)=>
{
  let randPass=generatePassword();
  let file= await addFile(upload.name, upload.mimetype,user_id,randPass,upload.size,upload.encoding,upload.md5,upload.truncated)
  file=file.toJSON()
  let readStream=bufferToStream(upload.data)
  const gzipStream = zlib.createGzip();
  const writeStream = fs.createWriteStream('./uploads/'+file.id+'.enc');

  key=key+randPass;
  const CIPHER_KEY=getCipherKey(key);
  const initVect = crypto.randomBytes(16);
  const cipher = crypto.createCipheriv('aes256', CIPHER_KEY, initVect);
  const appendInitVect = new  AppendInitVect(initVect);

  readStream
  .pipe(gzipStream)
  .pipe(cipher)
  .pipe(appendInitVect)
  .pipe(writeStream);
  return file;

}


const decrypt = function(text, cipherKey,initVect){
  var decipher = crypto.createDecipheriv('aes256', cipherKey, initVect);
  try {
      var dec = decipher.update(text,'hex','utf8');
      dec += decipher.final('utf8');
      return dec;
  } catch (ex) {
      console.log('failed');
      return;
  }
}

const downloadFile=async(filePath,key,res,file)=>
{
  try{
  const readIv = fs.createReadStream(filePath, { end: 15 });
  let initVect;
  readIv.on('data', (chunk) => {
  initVect = chunk;
  });

  readIv.on('close', () => {
    try{
    const readStream = fs.createReadStream(filePath, { start: 16 });
    const cipherKey = getCipherKey(key);
    const decipher = crypto.createDecipheriv('aes256', cipherKey, initVect);
    const unzip = zlib.createUnzip();
    res.set('Content-disposition', 'attachment; filename=' + file.name);
    res.set('Content-Type', file.format);
    var data;
    try{
      decipher.on('error',(error)=>
      {
        console.log("exception while decrypting ",error);
        //res.status(500).json({message:"exception while decrypting the file, please add the correct key"});
        return

      }
      )
      unzip.on('error',(error)=>
      {
        console.log("exception while decrypting ",error);
        res.status(500).json({message:"exception while decrypting the file, please add the correct key"});
        return

      }
      )
      var data=readStream.pipe(decipher)
      console.log("data ",data);
      data.pipe(unzip).pipe(res)
      return
    
    }
    catch(exp)
    {
      console.log("exception while decrypting ",exp);
      res.status(500).json({message:"exception while decrypting the file, please add the correct key"});
      return
    }
    // try{
     
    // }
    // catch(exp)
    // {
    //   console.log("exception while decrypting ",exp);
    //   res.status(500).json({message:"exception while decrypting the file, please add the correct key"});
    // }
    }
    catch(exp)
    {
      res.status(500).json({message:"exception while decrypting the file, please add the correct key"});
      return

    }

  });

}
catch(exp)
{
  res.status(500).json({message:"exception while decrypting the file, please add the correct key"});
}
}

module.exports={upload,addFile,getFiles,generatePassword,AppendInitVect,encAndaddFile,downloadFile};