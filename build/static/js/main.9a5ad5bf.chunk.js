(this.webpackJsonpgameclient=this.webpackJsonpgameclient||[]).push([[0],{190:function(e,t,a){e.exports=a(386)},195:function(e,t,a){},197:function(e,t,a){},363:function(e,t,a){},383:function(e,t,a){},384:function(e,t,a){},385:function(e,t,a){},386:function(e,t,a){"use strict";a.r(t);var r=a(0),n=a.n(r),l=a(6),o=a.n(l),i=(a(195),a(53)),s=(a(196),a(197),a(24)),c=a.n(s),m=a(52),u=a(20),p=a(5),g=a(42),d=a(30),h=a(35),E=a(141),f=a(40),y=a.n(f);function v(e,t,a){var r=new Date;a||(a=30),r.setTime(r.getTime()+24*a*60*60*1e3);var n="expires="+r.toUTCString();document.cookie=e+"="+t+";"+n+";path=/"}function x(e){for(var t=e+"=",a=decodeURIComponent(document.cookie).split(";"),r=0;r<a.length;r++){for(var n=a[r];" "===n.charAt(0);)n=n.substring(1);if(0===n.indexOf(t))return n.substring(t.length,n.length)}return""}function b(e){document.cookie=e+"= ; expires =Thu, 01 Jan 1970 00:00:00 GMT ;path=/"}var w=function(e){if(!(e.token&&e.username&&e.id))throw"data not found";v("token",e.token,30),v("username",e.username,30),v("id",e.id)},T=function(){return{headers:{authorization:"bearer "+x("token")}}},R=function(){var e=x("token");return e||void 0},k=function(e){return{user:e.user,serverConfig:e.serverConfig}};a(215).config(),console.log("server url",Object({NODE_ENV:"production",PUBLIC_URL:"",REACT_APP_DEVELOPMENT_SERVER_URL:"http://localhost:3001/api/",REACT_APP_PRODUCTION_SERVER_URL:"https://prabugame.herokuapp.com/api/",REACT_APP_TEST_STRIPE_PK:"pk_test_9radQ2WXLrFFlDGV0XqNMUkw00PoyDNs29",REACT_APP_PRODUCTION_STRIPE_PK:"pk_live_iyPbhH04G2xJnlJwIi4gCQPi00y2HagUM9"}));var I="https://prabugame.herokuapp.com/api/",P=I+"signup",N=Object(i.withRouter)(u.a.create({name:"Signup"})((function(e){var t=e.form,a=t.getFieldDecorator,l=t.getFieldsError,o=t.getFieldError,i=t.isFieldTouched,s=Object(r.useState)(!1),f=Object(m.a)(s,2),v=f[0],x=f[1],b=Object(r.useState)(i("email")&&o("email")),T=Object(m.a)(b,2),R=T[0],k=T[1];Object(r.useEffect)((function(){e.form.validateFields()}),[]);var I=i("password")&&o("password"),N=i("plan")&&o("plan"),C=E.a.Option;var F;return n.a.createElement("div",null,n.a.createElement(u.a,{onSubmit:function(t){t.preventDefault(),x(!0),console.log("form",t.target.email),e.form.validateFields((function(t,a){var r;return c.a.async((function(n){for(;;)switch(n.prev=n.next){case 0:if(t){n.next=7;break}return console.log("Received values of form: ",a),n.next=4,c.a.awrap((l=a,y.a.post(P,l).then((function(e){return e})).catch((function(e){return e.response}))));case 4:200===(r=n.sent).status?r.data.token?(w(r.data),h.a.success("welcome "+r.data.username+" !"),x(!1),e.history.push("/home")):(x(!1),e.history.push("/login")):(h.a.error("Exception while signing us please try again later"),x(!1)),console.log("signup res",r);case 7:case"end":return n.stop()}var l}))}))},className:"login-form"},n.a.createElement(u.a.Item,{validateStatus:R?"error":"",help:R||""},a("email",{rules:[{required:!0,message:"Please input mailID"},{type:"email",message:"Please enter the proper E-Mail ID"}]})(n.a.createElement(g.a,{prefix:n.a.createElement(p.a,{type:"user",style:{color:"rgba(0,0,0,.25)"}}),size:"large",placeholder:"Email",onBlur:function(e){var t;return c.a.async((function(a){for(;;)switch(a.prev=a.next){case 0:if(console.log("isExist ",e.target.value),!e.target.value){a.next=7;break}return a.next=4,c.a.awrap((r=e.target.value,y.a.get(P+"/exist?email="+r,null).then((function(e){return e})).catch((function(e){return e.response}))));case 4:t=a.sent,console.log("exist ",t),200===t.status&&(t.data.status?k("email-ID already exist"):k(!1));case 7:case"end":return a.stop()}var r}))}}))),n.a.createElement(u.a.Item,{validateStatus:I?"error":"",help:I||""},a("password",{rules:[{required:!0,message:"Please input your Password!"}]})(n.a.createElement(g.a,{prefix:n.a.createElement(p.a,{type:"lock",style:{color:"rgba(0,0,0,.25)"}}),size:"large",type:"password",placeholder:"Password"}))),n.a.createElement(u.a.Item,{validateStatus:N?"error":"",help:N||""},a("plan",{rules:[{required:!0,message:"Please select a plan"}]})(n.a.createElement(E.a,{size:"large"},n.a.createElement(C,{value:"planA"},"plan A, 8 User, 3USD/month"),n.a.createElement(C,{value:"planB"},"plan B, 12 User, 5USD/month")))),n.a.createElement(u.a.Item,null,n.a.createElement("br",null),n.a.createElement(d.a,{type:"primary",htmlType:"submit",size:"large",className:"login-form-button",disabled:(F=l(),console.log("fieldError",F),Object.keys(F).some((function(e){return F[e]}))),loading:v},!v&&"Start Your Free Trial",v&&"Signing You In"))))}))),C=a(112),F=a(21),S=a(11),O=a(16),j=a(59),A=a(14),D=a(91),_=a(77),U=a(184),q=a(185),L=a.n(q),z=(a(363),a(68)),H=a(187),B=a(47),W=a(29),K=Object(B.b)(k,{})(Object(i.withRouter)((function(e){var t=O.a.Header;console.log("header",e);var a=R(),r=n.a.createElement(z.a,{theme:a?"light":"dark",mode:!a&&"horizontal",defaultSelectedKeys:e.defaultSelectedKeys,className:!a&&"header",style:{lineHeight:"64px",maxWidth:"200px"}},a?n.a.createElement(z.a.Item,{key:"1",style:{minWidth:"100px"},onClick:function(){return e.history.push("/user")}},"Account"):n.a.createElement(z.a.Item,{key:"1",style:{minWidth:"100px"},onClick:function(){return e.history.push("/login")}},"LOGIN"),n.a.createElement(z.a.Item,{key:"2",style:{minWidth:"100px"},onClick:function(){return e.history.push("/faq")}},"FAQ"),a&&n.a.createElement(z.a.Divider,null),a&&n.a.createElement(z.a.Item,{key:"3",style:{minWidth:"100px",color:"red"},onClick:function(){return e.history.push("/logout")}},"Logout"));return n.a.createElement(t,{style:{position:"fixed",zIndex:1,width:"100%",background:"rgba(80, 80, 80, 0.7)"}},n.a.createElement(W.b,{to:"/"}," ",n.a.createElement("div",{className:"logo"})),n.a.createElement(F.a,{type:"flex",justify:"end",align:"top"},n.a.createElement(S.a,null,!a&&r,a&&n.a.createElement(n.a.Fragment,null,n.a.createElement(j.a,{shape:"square",size:"large",style:{margin:10,backgroundColor:"orange",verticalAlign:"middle"}}," ",e.user.username[0].toUpperCase()),n.a.createElement(H.a.Button,{size:"large",overlay:r,onClick:function(){return e.history.push("/user")}},"Account")))))}))),G=L()((function(e){var t=O.a.Content,a=O.a.Footer,r=A.a.Title,l=A.a.Paragraph;return n.a.createElement(O.a,{className:"parallax",style:{backgroundImage:"../media/bg.jpg"}},n.a.createElement(K,null),n.a.createElement(t,{style:{padding:"0 0 0 0px",marginTop:64,minHeight:"1020px"}},n.a.createElement(F.a,{style:{minHeight:"720px"}},n.a.createElement(S.a,{span:16},n.a.createElement(U.a,{dotPosition:"left",autoplay:!0,style:{margin:15,marginTop:30,minHeight:700,minWidth:400,opacity:.5,color:"white"}},n.a.createElement("div",{style:{minHeight:700,minWidth:400,color:"white"}},n.a.createElement("h3",null,"we are here to tell some awesome things about our product")),n.a.createElement("div",null,n.a.createElement("h3",null,"we are here to tell some awesome things about our product")),n.a.createElement("div",null,n.a.createElement("h3",null,"we are here to tell some awesome things about our product")),n.a.createElement("div",null,n.a.createElement("h3",null,"we are here to tell some awesome things about our product")))),n.a.createElement(S.a,{span:7,style:{margin:"15px",marginTop:"120px",background:"rgba(80, 80, 80, 0.5)",paddingLeft:"40px",paddingRight:"40px",paddingBottom:"50px",paddingTop:"50px"}},n.a.createElement(N,null))),n.a.createElement(F.a,{style:Object(C.a)({minHeight:400,margin:"0px",minWidth:"100%",backgroundColor:"white",padding:0},"margin",0)},n.a.createElement(S.a,null,n.a.createElement(F.a,{type:"flex",justify:"center",style:{paddingBottom:"8px"}},n.a.createElement(S.a,null,n.a.createElement(r,{level:3},"Why Us?"))),n.a.createElement(F.a,{type:"flex",justify:"space-between",style:{marginLeft:"15px",marginRight:"15px"}},n.a.createElement(S.a,{span:6,className:"alignCenter"},n.a.createElement(j.a,{shape:"square",size:64,icon:"user"}),n.a.createElement(r,{level:4},"Title"),n.a.createElement(l,null,"we are paragraph write someThing about me")),n.a.createElement(S.a,{span:6,className:"alignCenter"},n.a.createElement(j.a,{shape:"square",size:64,icon:"user"}),n.a.createElement(r,{level:4},"Title"),n.a.createElement(l,null,"we are paragraph write someThing about me")),n.a.createElement(S.a,{span:6,className:"alignCenter"},n.a.createElement(j.a,{shape:"square",size:64,icon:"user"}),n.a.createElement(r,{level:4},"Title"),n.a.createElement(l,null,"we are paragraph write someThing about me")),n.a.createElement(S.a,{span:6,className:"alignCenter"},n.a.createElement(j.a,{shape:"square",size:64,icon:"user"}),n.a.createElement(r,{level:4},"Title"),n.a.createElement(l,null,"we are paragraph write someThing about me")),n.a.createElement(S.a,{span:6,className:"alignCenter"},n.a.createElement(j.a,{shape:"square",size:64,icon:"user"}),n.a.createElement(r,{level:4},"Title"),n.a.createElement(l,null,"we are paragraph write someThing about me"))))),n.a.createElement(F.a,{style:{minHeight:200,margin:"0px",minWidth:"100%",padding:0,marginTop:"50px",marginBottom:"50px"}},n.a.createElement(S.a,null,n.a.createElement(r,{className:"alignCenter",style:{color:"white"}},"Pricing"),n.a.createElement(F.a,{style:{marginBottom:"50px",marginLeft:"40px",marginTop:"50px"}},n.a.createElement(S.a,{span:6},n.a.createElement(D.a,{title:n.a.createElement(r,{level:4,className:"alignCenter",style:{color:"white"}},"Price 1"),className:"header",style:{width:350,minHeight:400,color:"white"}},n.a.createElement("p",null,"Card content"),n.a.createElement("p",null,"Card content"),n.a.createElement("p",null,"Card content"))),n.a.createElement(S.a,{span:6},n.a.createElement(D.a,{title:n.a.createElement(r,{level:4,className:"alignCenter",style:{color:"white"}},"Price 2"),className:"header",style:{width:350,minHeight:400,color:"white"}},n.a.createElement("p",null,"Card content"),n.a.createElement("p",null,"Card content"),n.a.createElement("p",null,"Card content"))),n.a.createElement(S.a,{span:6},n.a.createElement(D.a,{title:n.a.createElement(r,{level:4,className:"alignCenter",style:{color:"white"}},"Price 3"),className:"header",style:{width:350,minHeight:400,color:"white"}},n.a.createElement("p",null,"Card content"),n.a.createElement("p",null,"Card content"),n.a.createElement("p",null,"Card content"))),n.a.createElement(S.a,{span:6},n.a.createElement(D.a,{title:n.a.createElement(r,{level:4,className:"alignCenter",style:{color:"white"}},"Price 4"),className:"header",style:{width:350,minHeight:400,color:"white"},bordered:"false"},n.a.createElement(_.a,{style:{color:"white"}},n.a.createElement(_.a.Item,null,"value1"),n.a.createElement(_.a.Item,null,"value2"),n.a.createElement(_.a.Item,null,"value3"),n.a.createElement(_.a.Item,null,"value4")))))))),n.a.createElement(a,null,n.a.createElement("div",{style:{textAlign:"left"}},"  For any enquiries, contact prabumohan96@gmail.com"),n.a.createElement("div",{style:{textAlign:"right"}}," \xa9 2020, All Rights Reserved.")))})),M=a(147),J=(a(383),function(e){var t=O.a.Content,a=O.a.Footer,r=M.a.Panel,l=A.a.Title,o=n.a.createElement("p",{style:{paddingLeft:24}},"A dog is a type of domesticated animal. Known for its loyalty and faithfulness, it can be found as a welcome guest in many households across the world.");return n.a.createElement(O.a,{className:"parallax",style:{backgroundImage:"../media/bg.jpg"}},n.a.createElement(K,{defaultSelectedKeys:["2"]}),n.a.createElement(t,{style:Object(C.a)({padding:"0 0 0 0px",marginTop:64,minHeight:"1020px",marginLeft:"80px",marginRight:"80px"},"marginTop","120px")},n.a.createElement(l,{className:"alignCenter",style:{color:"white"}},"FAQ"),n.a.createElement(M.a,{bordered:!1,defaultActiveKey:["1"]},n.a.createElement(r,{header:"This is panel header 1",key:"1"},o),n.a.createElement(r,{header:"This is panel header 2",key:"2"},o),n.a.createElement(r,{header:"This is panel header 3",key:"3"},o))),n.a.createElement(a,null,n.a.createElement("div",{style:{textAlign:"left"}},"  For any enquiries, contact prabumohan96@gmail.com"),n.a.createElement("div",{style:{textAlign:"right"}}," \xa9 2020, All Rights Reserved.")))}),V=Object(B.b)(k,{setUserDetailsToStore:function(e){return{type:"USER_INIT",data:e}}})(Object(i.withRouter)(u.a.create({name:"Login"})((function(e){var t=Object(r.useState)(!1),a=Object(m.a)(t,2),l=a[0],o=a[1],i=e.form,s=i.getFieldDecorator,E=(i.getFieldsError,i.getFieldError),f=i.isFieldTouched;Object(r.useEffect)((function(){e.form.validateFields()}),[]);var v=f("email")&&E("email"),x=f("password")&&E("password"),b=A.a.Title;A.a.Paragraph;return n.a.createElement("div",null,n.a.createElement(b,{level:3,style:{color:"white"}},"Login"),n.a.createElement("br",null),n.a.createElement(u.a,{onSubmit:function(t){t.preventDefault(),console.log("type",o),o(!0),e.form.validateFields((function(t,a){var r;return c.a.async((function(n){for(;;)switch(n.prev=n.next){case 0:if(t){n.next=6;break}return console.log("Received values of form: ",a),n.next=4,c.a.awrap((l=a,console.log("loginData ",l),y.a.post("https://prabugame.herokuapp.com/api/login",l).then((function(e){return e})).catch((function(e){return e.response}))));case 4:(r=n.sent)&&200===r.status?(w(r.data),e.setUserDetailsToStore(r.data),console.log("data =>",e.user),h.a.success("welcome "+r.data.username+" ! "),o(!1),e.history.push("/")):(console.log("login Response",r),h.a.error("Exception while Signing-in "),o(!1));case 6:case"end":return n.stop()}var l}))}))},className:"login-form"},console.log("from return",e.user.username),n.a.createElement(u.a.Item,{validateStatus:v?"error":"",help:v||""},s("email",{rules:[{required:!0,message:"Please input mailID"},{type:"email",message:"Please enter the proper E-Mail ID"}]})(n.a.createElement(g.a,{prefix:n.a.createElement(p.a,{type:"user",style:{color:"rgba(0,0,0,.25)"}}),size:"large",placeholder:"Email"}))),n.a.createElement(u.a.Item,{validateStatus:x?"error":"",help:x||""},s("password",{rules:[{required:!0,message:"Please input your Password!"}]})(n.a.createElement(g.a,{prefix:n.a.createElement(p.a,{type:"lock",style:{color:"rgba(0,0,0,.25)"}}),size:"large",type:"password",placeholder:"Password"}))),n.a.createElement(u.a.Item,null,n.a.createElement("br",null),n.a.createElement(d.a,{type:"primary",htmlType:"submit",size:"large",className:"login-form-button",loading:l},!l&&"Login",l&&"Loging You In"))),n.a.createElement(W.b,{className:"login-form-forgot",to:"/forgotpassword"},"Forgot password"))})))),Q=function(e){var t=O.a.Content,a=O.a.Footer;A.a.Title,A.a.Paragraph;return n.a.createElement(O.a,{className:"parallax",style:{backgroundImage:"../media/bg.jpg",height:"calc(100vw)"}},n.a.createElement(K,{defaultSelectedKeys:["1"]}),n.a.createElement(t,{style:{padding:"0 0 0 0px",marginTop:64,height:"720px"}},n.a.createElement(F.a,{className:"alignCenter"},n.a.createElement(S.a,{span:8}),n.a.createElement(S.a,{span:8,style:{margin:"15px",marginTop:"120px",background:"rgba(80, 80, 80, 0.5)",paddingLeft:"40px",paddingRight:"40px",paddingBottom:"50px",paddingTop:"50px"}},n.a.createElement(V,null)),n.a.createElement(S.a,{span:8}))),n.a.createElement(a,null,n.a.createElement("div",{style:{textAlign:"left"}},"  For any enquiries, contact prabumohan96@gmail.com"),n.a.createElement("div",{style:{textAlign:"right"}}," \xa9 2020, All Rights Reserved.")))},Y=function(e){var t=O.a.Content,a=O.a.Footer;A.a.Title,A.a.Paragraph;return n.a.createElement(O.a,{className:"parallax",style:{backgroundImage:"../media/bg.jpg"}},n.a.createElement(K,{defaultSelectedKeys:["1"],isLoggedIn:"true"}),n.a.createElement(t,{style:{padding:"0 0 0 0px",marginTop:64,minHeight:"720px"}}),n.a.createElement(a,null,n.a.createElement("div",{style:{textAlign:"left"}},"  For any enquiries, contact prabumohan96@gmail.com"),n.a.createElement("div",{style:{textAlign:"right"}}," \xa9 2020, All Rights Reserved.")))},X=(a(384),Object(i.withRouter)((function(e){var t=!1;return Object(r.useEffect)((function(){c.a.async((function(a){for(;;)switch(a.prev=a.next){case 0:return a.next=2,c.a.awrap(y.a.post("https://prabugame.herokuapp.com/api/logout",{signout:!0},T()).then((function(e){return e})).catch((function(e){return e.response})));case 2:200===a.sent.status?(b("token"),b("username"),b("id"),e.history.push("/")):t=!0;case 4:case"end":return a.stop()}}))})),n.a.createElement("div",null,!t&&n.a.createElement(A.a.Title,{className:"center"}," signing you out !!!"),t&&n.a.createElement(n.a.Fragment,null,n.a.createElement(A.a.Title,null," ohh!!, we encountered a exception while signing you out"),"  ",n.a.createElement(A.a.Paragraph,null,"If the Exception persist please contact the support")))}))),$=a(67),Z=function(){var e=arguments.length>0&&void 0!==arguments[0]?arguments[0]:[],t=arguments.length>1?arguments[1]:void 0;return"CONFIG_INIT"===t.type?t.data:"CONFIG_ADD"===t.type?e.concat(t.data):e},ee=function(){var e=arguments.length>0&&void 0!==arguments[0]?arguments[0]:[],t=arguments.length>1?arguments[1]:void 0;return"USER_INIT"===t.type?(console.log("action",t),t.data):e},te=a(186),ae=Object($.c)({serverConfig:Z,user:ee}),re=Object($.d)(ae,Object($.a)(te.a)),ne=function(e){var t=O.a.Content,a=O.a.Footer;A.a.Title,A.a.Paragraph;return n.a.createElement(O.a,{className:"parallax",style:{backgroundImage:"../media/bg.jpg"}},n.a.createElement(K,{defaultSelectedKeys:["1"],isLoggedIn:"true"}),n.a.createElement(t,{style:{padding:"0 0 0 0px",marginTop:64,minHeight:"720px"}}),n.a.createElement(a,null,n.a.createElement("div",{style:{textAlign:"left"}},"  For any enquiries, contact prabumohan96@gmail.com"),n.a.createElement("div",{style:{textAlign:"right"}}," \xa9 2020, All Rights Reserved.")))},le=(a(385),function(e){return console.log("not found"),n.a.createElement("div",{id:"notfound"},n.a.createElement("div",{class:"notfound"},n.a.createElement("div",{class:"notfound-404"},n.a.createElement("h1",null,"Oops!"),n.a.createElement("h2",null,"404 - The Page can't be found")),n.a.createElement(W.b,{to:"/"},"Go TO Homepage")))}),oe=Object(B.b)(k,{setUserDetailsToStore:function(e){return{type:"USER_INIT",data:e}}})(Object(i.withRouter)(u.a.create({name:"forgotpassword"})((function(e){var t=Object(r.useState)(!1),a=Object(m.a)(t,2),l=a[0],o=a[1],i=e.form,s=i.getFieldDecorator,E=(i.getFieldsError,i.getFieldError),f=i.isFieldTouched;Object(r.useEffect)((function(){e.form.validateFields()}),[]);var v=f("email")&&E("email"),x=A.a.Title,b=A.a.Paragraph;return n.a.createElement("div",null,n.a.createElement(x,{level:3,style:{color:"white"}},"Forgot Password"),n.a.createElement("br",null),n.a.createElement(u.a,{onSubmit:function(t){t.preventDefault(),console.log("type",o),o(!0),e.form.validateFields((function(t,a){var r;return c.a.async((function(n){for(;;)switch(n.prev=n.next){case 0:if(t){n.next=6;break}return console.log("Received values of form: ",a),n.next=4,c.a.awrap((l=a,y.a.post("https://prabugame.herokuapp.com/api/forgotpassword",l).then((function(e){return e})).catch((function(e){return e.response}))));case 4:(r=n.sent)&&200===r.status?(console.log("data =>",e.user),h.a.success("password reset link has been sent to your registered mail-id"),o(!1),e.history.push("/")):(console.log("forgot Response",r),h.a.error("Exception while Signing-in "),o(!1));case 6:case"end":return n.stop()}var l}))}))},className:"login-form"},console.log("from return",e.user.username),n.a.createElement(u.a.Item,{validateStatus:v?"error":"",help:v||""},s("email",{rules:[{required:!0,message:"Please input mailID"},{type:"email",message:"Please enter the proper E-Mail ID"}]})(n.a.createElement(g.a,{prefix:n.a.createElement(p.a,{type:"user",style:{color:"rgba(0,0,0,.25)"}}),size:"large",placeholder:"Email"}))),n.a.createElement(u.a.Item,null,n.a.createElement(d.a,{type:"primary",htmlType:"submit",size:"large",className:"login-form-button",loading:l},!l&&"Sent password reset link",l&&"sending password reset link"))),n.a.createElement(W.b,{to:"/login"},n.a.createElement(p.a,{type:"arrow-left"}),n.a.createElement(b,{style:{color:"white"}}," Back to Login")))})))),ie=function(e){var t=O.a.Content,a=O.a.Footer;A.a.Title,A.a.Paragraph;return n.a.createElement(O.a,{className:"parallax",style:{backgroundImage:"../media/bg.jpg",height:"calc(100vw)"}},n.a.createElement(K,{defaultSelectedKeys:["1"]}),n.a.createElement(t,{style:{padding:"0 0 0 0px",marginTop:64,height:"720px"}},n.a.createElement(F.a,{className:"alignCenter"},n.a.createElement(S.a,{span:8}),n.a.createElement(S.a,{span:8,style:{margin:"15px",marginTop:"120px",background:"rgba(80, 80, 80, 0.5)",paddingLeft:"40px",paddingRight:"40px",paddingBottom:"50px",paddingTop:"50px"}},n.a.createElement(oe,null)),n.a.createElement(S.a,{span:8}))),n.a.createElement(a,null,n.a.createElement("div",{style:{textAlign:"left"}},"  For any enquiries, contact prabumohan96@gmail.com"),n.a.createElement("div",{style:{textAlign:"right"}}," \xa9 2020, All Rights Reserved.")))},se=Object(B.b)(k,{setUserDetailsToStore:function(e){return{type:"USER_INIT",data:e}}})(Object(i.withRouter)(u.a.create({name:"resetPassword"})((function(e){var t=Object(r.useState)(!1),a=Object(m.a)(t,2),l=a[0],o=a[1],i=e.form,s=i.getFieldDecorator,E=(i.getFieldsError,i.getFieldError),f=i.isFieldTouched;Object(r.useEffect)((function(){e.form.validateFields()}),[]);var v=f("email")&&E("email"),x=f("password")&&E("password"),b=A.a.Title;A.a.Paragraph;console.log("location ",e.location),console.log("match ",e.match),Object(r.useEffect)((function(){!function(){var t;c.a.async((function(a){for(;;)switch(a.prev=a.next){case 0:if(!(t=e.location.search).includes("token=")||t.includes("&")||!t.split("=")[1]){a.next=7;break}return a.next=4,c.a.awrap((r=t.split("=")[1],y.a.post("https://prabugame.herokuapp.com/api/forgotpassword/verifykey?token="+r).then((function(e){return e})).catch((function(e){return e.response}))));case 4:a.sent,a.next=8;break;case 7:h.a.error("invalid token redirecting to home page");case 8:case"end":return a.stop()}var r}))}()}),[]);return n.a.createElement("div",null,n.a.createElement(b,{level:3,style:{color:"white"}},"Reset Password"),n.a.createElement("br",null),n.a.createElement(u.a,{onSubmit:function(t){t.preventDefault(),console.log("sumbit"),e.form.validateFields((function(t,a){var r,n;return c.a.async((function(l){for(;;)switch(l.prev=l.next){case 0:if(console.log("values ",a),t){l.next=13;break}if(o(!0),console.log("Received values of form: ",a),!(r=e.location.search).includes("token=")||r.includes("&")||!r.split("=")[1]){l.next=12;break}return l.next=8,c.a.awrap((i={token:r.split("=")[1],password:a.password},y.a.post("https://prabugame.herokuapp.com/api/resetpass",i).then((function(e){return e})).catch((function(e){return e.response}))));case 8:(n=l.sent)&&200===n.status?(console.log("data =>",e.user),h.a.success("Your password changed please login "),o(!1),e.history.push("/login")):(console.log("login Response",n),h.a.error("Exception while resetting your password"),o(!1)),l.next=13;break;case 12:h.a.error("Exception while resetting your password");case 13:case"end":return l.stop()}var i}))}))},className:"login-form"},console.log("from return",e.user.username),n.a.createElement(u.a.Item,{validateStatus:v?"error":"",help:v||""},s("email",{})(n.a.createElement(g.a,{prefix:n.a.createElement(p.a,{type:"user",style:{color:"rgba(0,0,0,.25)"}}),size:"large",placeholder:"Email"}))),n.a.createElement(u.a.Item,{validateStatus:x?"error":"",help:x||""},s("password",{rules:[{required:!0,message:"Please input your Password!"}]})(n.a.createElement(g.a,{prefix:n.a.createElement(p.a,{type:"lock",style:{color:"rgba(0,0,0,.25)"}}),size:"large",type:"password",placeholder:"Password"}))),n.a.createElement(u.a.Item,null,n.a.createElement(d.a,{type:"primary",htmlType:"submit",size:"large",className:"login-form-button",loading:l},!l&&"Reset Password",l&&"Resetting Password"))),n.a.createElement(W.b,{className:"login-form-forgot",to:"/forgotpassword"},"Forgot password"))})))),ce=function(e){var t=O.a.Content,a=O.a.Footer;A.a.Title,A.a.Paragraph;return n.a.createElement(O.a,{className:"parallax",style:{backgroundImage:"../media/bg.jpg",height:"calc(100vw)"}},n.a.createElement(K,{defaultSelectedKeys:["1"]}),n.a.createElement(t,{style:{padding:"0 0 0 0px",marginTop:64,height:"720px"}},n.a.createElement(F.a,{className:"alignCenter"},n.a.createElement(S.a,{span:8}),n.a.createElement(S.a,{span:8,style:{margin:"15px",marginTop:"120px",background:"rgba(80, 80, 80, 0.5)",paddingLeft:"40px",paddingRight:"40px",paddingBottom:"50px",paddingTop:"50px"}},n.a.createElement(se,null)),n.a.createElement(S.a,{span:8}))),n.a.createElement(a,null,n.a.createElement("div",{style:{textAlign:"left"}},"  For any enquiries, contact prabumohan96@gmail.com"),n.a.createElement("div",{style:{textAlign:"right"}}," \xa9 2020, All Rights Reserved.")))};var me=Object(i.withRouter)((function(e){return null!==re.user&&void 0!==re.user||(console.log("store user added"),function(e){var t={username:x("username"),token:x("token"),id:x("id")};e.dispatch({type:"USER_INIT",data:t})}(re)),n.a.createElement(B.a,{store:re},console.log("store =>",re),n.a.createElement(i.Switch,{location:e.location},n.a.createElement(i.Route,{exact:!0,path:"/",render:function(){return R()?n.a.createElement(i.Redirect,{to:"/home"}):n.a.createElement(G,null)}}),n.a.createElement(i.Route,{exact:!0,path:"/faq",render:function(){return n.a.createElement(J,null)}}),n.a.createElement(i.Route,{exact:!0,path:"/login",render:function(){return R()?n.a.createElement(i.Redirect,{to:"/home"}):n.a.createElement(Q,null)}}),n.a.createElement(i.Route,{exact:!0,path:"/logout",render:function(){return R()?n.a.createElement(X,null):n.a.createElement(i.Redirect,{to:"/"})}}),n.a.createElement(i.Route,{exact:!0,path:"/home",render:function(){return R()?n.a.createElement(Y,null):n.a.createElement(i.Redirect,{to:"/"})}}),n.a.createElement(i.Route,{exact:!0,path:"/user",render:function(){return R()?n.a.createElement(ne,null):n.a.createElement(i.Redirect,{to:"/"})}}),n.a.createElement(i.Route,{exact:!0,path:"/forgotpassword",render:function(){return R()?n.a.createElement(i.Redirect,{to:"/home"}):n.a.createElement(ie,null)}}),n.a.createElement(i.Route,{exact:!0,path:"/resetPassword",render:function(){return R()?n.a.createElement(i.Redirect,{to:"/home"}):n.a.createElement(ce,null)}}),n.a.createElement(i.Route,{exact:!0,path:"/signup"},n.a.createElement(N,null)),n.a.createElement(i.Route,{exact:!0,path:"/pagenotfound",render:function(){return n.a.createElement(le,null)}}),n.a.createElement(i.Route,{render:function(){return n.a.createElement(i.Redirect,{to:"/pagenotfound"})}})))}));Boolean("localhost"===window.location.hostname||"[::1]"===window.location.hostname||window.location.hostname.match(/^127(?:\.(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)){3}$/));o.a.render(n.a.createElement(W.a,null,n.a.createElement(me,null)),document.getElementById("root")),"serviceWorker"in navigator&&navigator.serviceWorker.ready.then((function(e){e.unregister()}))}},[[190,1,2]]]);
//# sourceMappingURL=main.9a5ad5bf.chunk.js.map