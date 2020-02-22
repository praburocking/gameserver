//$Id$
package com.adventnet.crm.ignite.util;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import com.adventnet.adventnetcrmsfa.CRMCHARTCONFIG;
import com.adventnet.adventnetcrmsfa.CRMCONFIG;
import com.adventnet.adventnetcrmsfa.CRMDASHBOARD;
import com.adventnet.adventnetcrmsfa.CRMDEFAULTCV;
import com.adventnet.adventnetcrmsfa.CRMFIELD;
import com.adventnet.adventnetcrmsfa.CRMMODEFIELD;
import com.adventnet.adventnetcrmsfa.CRMOPPORTUNITYSTAGE;
import com.adventnet.adventnetcrmsfa.CRMPICKLISTLAYOUTREL;
import com.adventnet.adventnetcrmsfa.CRMPICKLISTVALUES;
import com.adventnet.adventnetcrmsfa.CRMSECTION;
import com.adventnet.adventnetcrmsfa.CRMTAB;
import com.adventnet.adventnetcrmsfa.CRMVIEWSETTINGS;
import com.adventnet.adventnetcrmsfa.CRMWORKFLOWRULE;
import com.adventnet.crm.activities.calls.NewCallsViewMigrator;
import com.adventnet.crm.authorization.util.SecurityUtil;
import com.adventnet.crm.common.util.ComboDBService;
import com.adventnet.crm.common.util.CrmConstants;
import com.adventnet.crm.common.util.CrmResourceBundle;
import com.adventnet.crm.common.util.CrmViewConfigUtil;
import com.adventnet.crm.common.util.PersistenceCacheUtil;
import com.adventnet.crm.common.util.ResourceUtil;
import com.adventnet.crm.customization.fields.util.FieldDependencyUtil;
import com.adventnet.crm.customization.relatedlist.util.RelatedListUtil;
import com.adventnet.crm.customization.section.util.SectionUtil;
import com.adventnet.crm.custommodule.util.CustomModuleJsonValidate;
import com.adventnet.crm.custommodule.util.CustomModuleUtil;
//import com.adventnet.crm.integration.emailstats.util.EditEmailTrackingUtil;
import com.adventnet.db.api.RelationalAPI;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.DataSet;
import com.adventnet.ds.query.DeleteQueryImpl;
import com.adventnet.ds.query.Join;
import com.adventnet.ds.query.QueryConstants;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.adventnet.ds.query.UpdateQuery;
import com.adventnet.ds.query.UpdateQueryImpl;
import com.adventnet.iam.IAMUtil;
import com.adventnet.iam.xss.IAMEncoder;
import com.adventnet.persistence.DataObject;
import com.adventnet.persistence.Persistence;
import com.adventnet.persistence.RELATIONALCRITERIA;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.SELECTQUERY;
import com.adventnet.persistence.xml.Xml2DoConverter;
import com.zoho.crm.core.authorization.users.UserUtil;
import com.zoho.crm.core.entity.util.CrmModuleConstants;
import com.zoho.crm.core.meta.fields.FieldUtil;
import com.zoho.crm.core.meta.layouts.PageLayoutUtil;
import com.zoho.crm.core.meta.sections.CoreSectionConstants;
import com.zoho.crm.core.meta.tabs.TabUtil;
import com.zoho.crm.core.security.util.OrgUtil;

/**
 * 
 * @author akshaya-2553
 */
public class CrmIgniteUtil {
	
	private static final Logger LOGGER = Logger.getLogger(CrmIgniteUtil.class.getName());
	
	//public HttpServletRequest request = ServletActionContext.getRequest();
	//public HttpServletResponse response = ServletActionContext.getResponse();
	
	public static boolean isZapierCall(HttpServletRequest request) {
		
		if(request != null && request.getHeader("X-ZOHO-SERVICE") != null && request.getHeader("X-ZOHO-SERVICE").equals("zapier")) {
			return true;
		}
		return false;
	}
	
	public void handleIgniteSignup() throws Exception
	{
		String zgId = OrgUtil.getCurrentZGID().toString();
		LOGGER.info("Ignite handleIgniteSignup called");
		
		// Layout fields modification. For Ignite we will remove some fields in standard Layout.
		modifyContactLayout(zgId);
		modifyAccountLayout(zgId);
		modifyDealLayout(zgId);
		modifyTaskLayout(zgId);
		modifyEventLayout(zgId);
		modifyCallLayout();
		modifyProductLayout(zgId);
		
		//Enable feeds for Ignite modules
		enableFeeds(zgId);
		
		//update leads,email and social tab presence
		updateTabPresence(zgId);
		
		// smart filters
		populateListViewStatsFilters(zgId);
		
		//Removing old dashboard data
		removeDashBoardData(zgId);
		
		//Populate Reports DO from xml
		populateReports(zgId);
		
		//To update the default workflow rule
		modifyDefaultWorkflowRule(zgId);
		
		//Products subform creation in deals
		//createProductsSubform(zgId);
		
		//Pipeline creation for Bigin user
		createStdPipeline();
			
		//Email Insights migration - Temporarily added
		/*try{
			EditEmailTrackingUtil.doInitialEmailInsightsMigration();
		}catch(Exception e){
			LOGGER.log(Level.SEVERE,"Exception while performing email insights initial migration for bigin user ::"+e);
		}*/
	}
	
	private void createStdPipeline() throws Exception{
		try{
			FieldDependencyUtil.createStandardPipelineForIgnite();
		}catch(Exception e){
			LOGGER.log(Level.WARNING,"Exception while creating standard pipeline for Bigin user ::{0}",e);
			throw e;
		}
	}
	
	private void updateTabPresence(String zgId) throws Exception{
		try{
			//Disable leads module for ignite
			TabUtil.updateTabPresense(CrmModuleConstants.LEADS, -1L, zgId);
				
			//Update social tab presence
			TabUtil.updateSocialTabPresence(1L);
				
			//Update email tab presence
			TabUtil.updateEmailsTabPresence(1L);
		}catch(Exception e){
			LOGGER.log(Level.WARNING,"Exception while updating tabs presence for Bigin user ::{0}",e);
			throw e;
		}
	}
	
	public void createProductsSubform(String zgid) throws Exception{
		try{
			DataObject fielddObj=FieldUtil.getFieldsDOForModule(CrmModuleConstants.POTENTIALS, zgid);
			Long userId=UserUtil.getUserIdByZUID(IAMUtil.getCurrentUser().getZUID(), zgid);
			Locale locale = UserUtil.getUserLangLocaleByUserId(userId);
			if(fielddObj!=null && !fielddObj.isEmpty()){
				String tabId=""+fielddObj.getRow(CRMFIELD.TABLE).get(CRMFIELD.TABID);
				Long layoutId=PageLayoutUtil.getSystemdefinedActiveLayout(Long.parseLong(tabId),zgid);
				String productsJson="{\"layoutInfo\": {\"tabId\":"+tabId+",\"layoutId\":"+layoutId+"}, \"completeMBInfo\": {\"sDtls\": {\"S3\": {\"etype\": \"subformField\", \"presence\": 1, \"gentype\": 1, \"tabtraversal\": 2, \"colcount\": 1, \"gtype\": \"gs\", \"label\": \"Associated Products\", \"deid\": \"S3\", \"isSubformSection\": true } }, \"sSeq\": {\"1\": 2, \"2\": 4, \"4\": 1, \"S3\": 3 }, \"fSeq\": {\"F17\": {\"par\": \"S3\", \"seq\": 1 }, \"F18\": {\"par\": \"S3\", \"seq\": 2 }, \"F19\": {\"par\": \"S3\", \"seq\": 3 } }, \"extraDomValues\": {} }, \"completeSubformInfo\": {\"SF1\": {\"mDtls\": {\"modName\": \"SubForm1\", \"sinLabel\": \"Associated Products\", \"pluLabel\": \"Associated Products\"}, \"fSeq\": {\"F1\": {\"par\": \"S3\", \"seq\": 1 }, \"F2\": {\"par\": \"S3\", \"seq\": 2 }, \"F3\": {\"par\": \"S3\", \"seq\": 3 }, \"F4\": {\"par\": \"S3\", \"seq\": 4 }, \"F5\": {\"par\": \"S3\", \"seq\": 5 },\"F8\": {\"par\": \"S3\", \"seq\": 6 } }, \"fDtls\": {\"F1\": {\"gentype\": 1, \"presence\": 1, \"label\": \"Product\", \"uitype\": 133, \"etype\": \"lookupField\", \"name\": \"lookup\", \"relatedlistlabel\": \"Related List Name\", \"gtype\": \"lf\", \"source\": 1, \"lookuptype\": \"Products\", \"lookupfilter\": false, \"showtooltip\": false, \"tooltipcontent\": \"\", \"tooltiptype\": 1, \"isFieldPropertyUpdated\": true, \"deid\": \"F1\", \"pdeid\": \"S3\"}, \"F2\": {\"gentype\": 1, \"presence\": 1, \"label\": \"List Price\", \"decimal\": \"2\", \"uitype\": 36, \"etype\": \"currencyField\", \"roundtype\": \"normal\", \"name\": \"currency\", \"precision\": \"0\", \"gtype\": \"df\", \"mxlen\": \"16\", \"source\": 1, \"showtooltip\": false, \"tooltipcontent\": \"\", \"tooltiptype\": 1, \"isFieldPropertyUpdated\": true, \"deid\": \"F2\", \"pdeid\": \"S3\"}, \"F3\": {\"gentype\": 1, \"presence\": 1, \"label\": \"Quantity\", \"decimal\": 2, \"uitype\": 38, \"etype\": \"decimalField\", \"name\": \"currency\", \"precision\": 0, \"gtype\": \"df\", \"mxlen\": 16, \"source\": 1, \"deid\": \"F3\", \"pdeid\": \"S3\"}, \"F4\": {\"gentype\": 1, \"presence\": 1, \"label\": \"Discount\", \"decimal\": 2, \"uitype\": 38, \"etype\": \"decimalField\", \"name\": \"currency\", \"precision\": 0, \"gtype\": \"df\", \"mxlen\": 16,\"source\": 1, \"deid\": \"F4\", \"pdeid\": \"S3\"}, \"F8\": {\"gentype\": 1, \"presence\": 1, \"label\": \"Product_Name\", \"uitype\": 1, \"mxlen\": \"120\",\"etype\": \"textField\", \"name\": \"string\", \"gtype\": \"df\",\"source\": 1, \"deid\": \"F8\", \"pdeid\": \"S3\"}, \"F5\": {\"gentype\": 1, \"presence\": 1, \"label\": \"Total\", \"decimal\": \"2\", \"uitype\": 36, \"etype\": \"currencyField\", \"roundtype\": \"normal\", \"name\": \"currency\", \"precision\": \"0\", \"gtype\": \"df\", \"mxlen\": \"16\", \"source\": 1, \"showtooltip\": false, \"tooltipcontent\": \"\", \"tooltiptype\": 1, \"isFieldPropertyUpdated\": true, \"deid\": \"F5\", \"pdeid\": \"S3\"}, \"F6\": {\"etype\": \"textField\", \"presence\": 0, \"name\": \"SMCREATORID\", \"uitype\": 20, \"fcategory\": 0, \"gentype\": 1, \"label\": \"Created By\", \"deid\": \"F6\", \"pdeid\": \"S3\"}, \"F7\": {\"etype\": \"longintegerField\", \"presence\": 0, \"name\": \"SERIAL_NUMBER\", \"uitype\": 62, \"fcategory\": 0, \"gentype\": 1, \"label\": \"S.NO\", \"deid\": \"F7\", \"pdeid\": \"S3\"}}, \"subFormProperties\": {\"presence\": 1, \"allowreorder\": true, \"deid\": \"S3\"}, \"dKey\": [\"F6\", \"F7\"] } } }";//No i18n
				JSONObject entireModuleInfo=new JSONObject(productsJson);
				JSONObject elementVsErr = new JSONObject();
				CustomModuleJsonValidate validatedObj=null;
				if(entireModuleInfo != null)
				{
					validatedObj=new CustomModuleJsonValidate(entireModuleInfo,"edit",Long.parseLong(tabId), SecurityUtil.isPersonalAccount(), locale,true,true,null,null);//NO I18N
					elementVsErr = validatedObj.getElementVsErr();
				}
				CustomModuleUtil.updateModuleDetails(entireModuleInfo, Long.parseLong(tabId), elementVsErr, userId, locale, zgid);
			}
		}catch(Exception e){
			LOGGER.log(Level.WARNING,"Exception while creating deals subform for Bigin user ::"+e);
			throw e;
		}
	}
	
	private void modifyDefaultWorkflowRule(String zgid) throws Exception{
		try{
			//select RelationalCriteria.queryid from CrmWorkFlowRule inner join RelationalCriteria on CrmWorkFlowRule.criteriaid=RelationalCriteria.queryid where CrmWorkFlowRule.category=0
			Persistence pers=PersistenceCacheUtil.getNonCachePersistenceHandle(zgid);
			Criteria cr3 = new Criteria(Column.getColumn(CRMWORKFLOWRULE.TABLE,CRMWORKFLOWRULE.CATEGORY),Integer.valueOf("0"),QueryConstants.EQUAL,true);
			cr3 = cr3.and(new Criteria(Column.getColumn(CRMWORKFLOWRULE.TABLE,CRMWORKFLOWRULE.RULENAME),"Big Deal Rule",QueryConstants.EQUAL,true));
			DataObject workflowObj=pers.get(CRMWORKFLOWRULE.TABLE, cr3);
			if(workflowObj!=null && !workflowObj.isEmpty()){
				Row workflowRow=workflowObj.getRow(CRMWORKFLOWRULE.TABLE);
				workflowRow.set(CRMWORKFLOWRULE.ACTIVE, false);
				workflowObj.updateRow(workflowRow);
				pers.update(workflowObj);
				String criteriaId=workflowRow.get(CRMWORKFLOWRULE.CRITERIAID).toString();
				Criteria cr=new Criteria(Column.getColumn(RELATIONALCRITERIA.TABLE, RELATIONALCRITERIA.QUERYID),criteriaId,QueryConstants.EQUAL);
				cr=cr.and(new Criteria(Column.getColumn(RELATIONALCRITERIA.TABLE, RELATIONALCRITERIA.COLUMNNAME),"PROBABILITY",QueryConstants.EQUAL));
				pers.delete(cr);
			}
		}catch(Exception e){
			LOGGER.log(Level.WARNING,"Exception while updating workflow rule criteria for Bigin user ::"+e);
			throw e;
		}
	}
	
	private void enableFeeds(String zgid) throws Exception{
		try{
			UpdateQuery updQry = new UpdateQueryImpl( CRMTAB.TABLE );
			String igniteModules[]={"Contacts","Accounts","Potentials","Tasks","Events","Calls","Activities"};//No I18N
		    Criteria cr = new Criteria(Column.getColumn(CRMTAB.TABLE, CRMTAB.NAME),igniteModules,QueryConstants.IN);
		    updQry.setCriteria(cr);
		    updQry.setUpdateColumn(CRMTAB.ISFEEDSREQUIRED,Boolean.TRUE);
		    PersistenceCacheUtil.getNonCachePersistenceHandle(zgid).update( updQry ); 
		}catch(Exception e){
			LOGGER.log(Level.SEVERE,"Exception while enabling feeds for Ignite Modules ::"+e);
			throw e;
		}
	}
	
	private void removeDashBoardData(String zgid) throws Exception{
		try{
	        Persistence persistence = PersistenceCacheUtil.getPersistenceLiteHandle(zgid);
	        DeleteQueryImpl chartqry = new DeleteQueryImpl(Table.getTable(SELECTQUERY.TABLE));
	        chartqry.addJoin(new Join(SELECTQUERY.TABLE, CRMCHARTCONFIG.TABLE, new String[]{SELECTQUERY.QUERYID}, new String[]{CRMCHARTCONFIG.QUERYID}, Join.INNER_JOIN));
	        persistence.delete(chartqry);
	        LOGGER.log(Level.SEVERE,"CRM Data deleted from CRMCHARTCONFIG and SELECTQUERY tables.");
	        
	        DeleteQueryImpl dashboardqry = new DeleteQueryImpl(Table.getTable(CRMDASHBOARD.TABLE));
	        persistence.delete(dashboardqry);
	        LOGGER.log(Level.SEVERE,"CRM Data deleted from CRMDASHBOARD table.");
	        
	        DeleteQueryImpl configqry = new DeleteQueryImpl(Table.getTable(CRMCONFIG.TABLE));
	        Criteria cr = new Criteria(Column.getColumn(CRMCONFIG.TABLE, CRMCONFIG.CONFIGTYPE),"Dashboard",QueryConstants.EQUAL);
	        configqry.setCriteria(cr);
	        persistence.delete(configqry);
	        LOGGER.log(Level.SEVERE,"CRM Data deleted from CRMCONFIG table.");
	        
		}catch(Exception e){
			LOGGER.log(Level.SEVERE,"Exception while removing crm dashboard data for ignite ::"+e);
			throw e;
		}
    
	}
	
	private void updatePicklistValues(String fieldId,Long layoutId, String columnName) throws Exception{
		try{
			DataObject pickObj=ComboDBService.getPickListDoByFieldId(Long.parseLong(fieldId), layoutId, null);
			List priorityArray=new ArrayList();
			List statusArray=new ArrayList();
			if(pickObj!=null && !pickObj.isEmpty()){
				Iterator pickItr=pickObj.getRows(CRMPICKLISTVALUES.TABLE);
				while(pickItr.hasNext()){
					Row pickRow=(Row) pickItr.next();
					String val=pickRow.get(CRMPICKLISTVALUES.ACTUALVALUE).toString();
					if(columnName!=null && columnName.equalsIgnoreCase("PRIORITY")){
						if(val!=null && (val.equalsIgnoreCase("Highest") || val.equalsIgnoreCase("Lowest"))){
							priorityArray.add(pickRow.get(CRMPICKLISTVALUES.UNIQUEID));
						}
					}else if(columnName!=null && columnName.equalsIgnoreCase("STATUS")){
						if(val!=null && (val.equalsIgnoreCase("Waiting on someone else"))){
							statusArray.add(pickRow.get(CRMPICKLISTVALUES.UNIQUEID));
						}
					}
				}
			}
			if(priorityArray!=null && !priorityArray.isEmpty()){
				updatePicklistPresence(layoutId,priorityArray,false);
			}
			if(statusArray!=null && !statusArray.isEmpty()){
				updatePicklistPresence(layoutId,statusArray,false);
			}
		}catch(Exception e){
			LOGGER.log(Level.SEVERE,"Exception while removing task picklistvalues for priority and status fields ::"+e);
			throw e;
		}
	}
	
	/*private void updateCallPicklistValues(String fieldId,Long layoutId, String columnName){
		try{
			List callresultsArray=new ArrayList();
			if(columnName!=null && columnName.equalsIgnoreCase("CALLRESULT")){
				callresultsArray.add("-None-");//No I18N
				callresultsArray.add("Interested");//No I18N
				callresultsArray.add("Not Interested");//No I18N
				callresultsArray.add("No response/Busy");//No I18N
				callresultsArray.add("Requested more info");//No I18N
				callresultsArray.add("Requested call back");//No I18N
				callresultsArray.add("Invalid number");//No I18N
			}
			if(callresultsArray!=null && !callresultsArray.isEmpty()){
				addCallsPicklistvalue(fieldId,callresultsArray,layoutId);
			}
		}catch(Exception e){
			LOGGER.log(Level.SEVERE,"Exception while removing picklistvalues for call result field ::"+e);
		}
	}*/
	
	public static void addCallsPicklistvalue(String fieldId,List pickListValue, Long layoutId)throws Exception
	{		
		SelectQuery pickListFldQry = new SelectQueryImpl(Table.getTable(CRMPICKLISTVALUES.TABLE));
		pickListFldQry.addSelectColumn(Column.getColumn(null, "*"));
		pickListFldQry.addJoin(new Join(Table.getTable(CRMPICKLISTVALUES.TABLE),Table.getTable(CRMPICKLISTLAYOUTREL.TABLE),new String[]{CRMPICKLISTVALUES.UNIQUEID},new String[]{CRMPICKLISTLAYOUTREL.UNIQUEID},Join.LEFT_JOIN));
		Criteria cr = new Criteria(Column.getColumn(CRMPICKLISTVALUES.TABLE, CRMPICKLISTVALUES.FIELDID),fieldId,QueryConstants.EQUAL);
		cr = FieldUtil.addPicklistPresenceCriteria(cr);
		pickListFldQry.setCriteria(cr);
		DataObject dataObj = PersistenceCacheUtil.getNonCachePersistenceHandle().get(pickListFldQry);
		Long maxSortbyId = 1l;
		if(dataObj==null || !dataObj.isEmpty()){
			for(int picklistcount=0;picklistcount<pickListValue.size();picklistcount++){
				Row row = new Row(CRMPICKLISTVALUES.TABLE);
				row.set(CRMPICKLISTVALUES.FIELDID,fieldId);
				row.set(CRMPICKLISTVALUES.VALUE,pickListValue);
				row.set(CRMPICKLISTVALUES.SORTORDERID,maxSortbyId++);
				row.set(CRMPICKLISTVALUES.ACTUALVALUE,pickListValue.get(picklistcount));
				dataObj.addRow(row);
				Criteria cr1 = new Criteria(Column.getColumn(CRMPICKLISTLAYOUTREL.TABLE, CRMPICKLISTLAYOUTREL.LAYOUTID),layoutId,QueryConstants.EQUAL);
				Iterator<Row> iter = dataObj.getRows(CRMPICKLISTLAYOUTREL.TABLE,cr1);
				int optionSize = 0;
				boolean isValueExist = false;
				while (iter.hasNext())
				{
					optionSize++;
					Row relRow = iter.next();
					if(relRow.get(CRMPICKLISTLAYOUTREL.UNIQUEID).equals(row.get(CRMPICKLISTVALUES.UNIQUEID)))
					{
						isValueExist = true;
					}
				}
				if(optionSize > 0 && !isValueExist)
				{
					Row layoutRelRow = new Row(CRMPICKLISTLAYOUTREL.TABLE);
					layoutRelRow.set(CRMPICKLISTLAYOUTREL.LAYOUTID,layoutId);
					layoutRelRow.set(CRMPICKLISTLAYOUTREL.UNIQUEID,row.get(CRMPICKLISTVALUES.UNIQUEID));
					layoutRelRow.set(CRMPICKLISTLAYOUTREL.SORTORDERID,optionSize+1);	
					dataObj.addRow(layoutRelRow);
				}
			}
		}
		PersistenceCacheUtil.getNonCachePersistenceHandle().update(dataObj);
	}

	
	public void updatePicklistPresence(Long layoutId, List picklistArr,boolean presence) throws Exception
	{
		//Relation for standard layout with this picklist value is deleted here.
		Criteria crt = new Criteria(Column.getColumn(CRMPICKLISTLAYOUTREL.TABLE, CRMPICKLISTLAYOUTREL.LAYOUTID), layoutId, QueryConstants.EQUAL);
		crt = crt.and(new Criteria(Column.getColumn(CRMPICKLISTLAYOUTREL.TABLE, CRMPICKLISTLAYOUTREL.UNIQUEID), picklistArr.toArray(), QueryConstants.IN));
		PersistenceCacheUtil.getNCPurePersistenceLiteHandle().delete(crt);
	}
	
	public void populateListViewStatsFilters(String zgId) throws Exception{
		
		//String PATH_FOR_CONF = new StringBuilder(CommonUtil.getProperty( "server.dir" )).append(File.separator).append("conf").append(File.separator).append("AdventNetCRMSFA").append(File.separator).toString();// No I18N
		try{
			
			 DataObject cvobj = Xml2DoConverter.transform(CrmConstants.PATH_FOR_SFA+"DefaultIgniteSMFilters.xml");//No I18N
			 PersistenceCacheUtil.getNonCachePersistenceHandle(zgId).add(cvobj);
			 LOGGER.info("Ignite default filters populated successfully for zgid : "+zgId);
			 
		}catch(Exception e){
			LOGGER.info("Error in filters population zgid : "+zgId+" reason : "+e);
			throw e;
		}
	}

	private void populateReports(String zgid) throws Exception
	{
		try{
			String fileName = "populate-ignite-dashboards.xml";//No I18N
			DataObject xmlDO = Xml2DoConverter.transform(CrmConstants.PATH_FOR_SFA+fileName);
			PersistenceCacheUtil.getNonCachePersistenceHandle(zgid).add(xmlDO);
		}catch(Exception e){
			LOGGER.log(Level.SEVERE,"Exception while populating dashboard reports for ignite ::"+e);
			throw e;
		}
	}
	
	public void modifyAccountLayout(String zgid) throws Exception{
		
		//String zgid=""+request.getAttribute( CrmConstants.ZGID_FROM_FILTER);
		try{
			Persistence per = PersistenceCacheUtil.getPersistenceLiteHandle(zgid);
			DataObject fielddObj=FieldUtil.getFieldsDOForModule(CrmModuleConstants.ACCOUNTS, zgid);
			if(fielddObj!=null && !fielddObj.isEmpty()){
				String tabId=""+fielddObj.getRow(CRMFIELD.TABLE).get(CRMFIELD.TABID);
				Long layoutId=PageLayoutUtil.getSystemdefinedActiveLayout(Long.parseLong(tabId),zgid);
				HashMap<String, Long> sectionPKVsSection_ID = SectionUtil.getSectionPKVsSection_IDMap(Long.valueOf(tabId), layoutId, null, zgid);
				Long section_IdForSection1 = null;
				Long section_IdForSection2 = null;
				Long section_IdForSection5 = null;
				if(sectionPKVsSection_ID != null){
					String pkKey = new StringBuilder().append(layoutId).append('_').append(CrmConstants.LAYOUT_DEFAULTMODE).append('_').append(1L).toString();
					section_IdForSection1 = sectionPKVsSection_ID.get(pkKey);
					pkKey = new StringBuilder().append(layoutId).append('_').append(CrmConstants.LAYOUT_DEFAULTMODE).append('_').append(2L).toString();
					section_IdForSection2 = sectionPKVsSection_ID.get(pkKey);
					pkKey = new StringBuilder().append(layoutId).append('_').append(CrmConstants.LAYOUT_DEFAULTMODE).append('_').append(5L).toString();
					section_IdForSection5 = sectionPKVsSection_ID.get(pkKey);
				}
				//Update Accounts tab name
				updateAccountsTab(tabId,zgid);
				
				HashMap seqMap =new HashMap();
				
				//Ignite Default Accounts columns
				seqMap.put("ACCOUNTNAME",1);
				seqMap.put("SMOWNERID",2);
				seqMap.put("PHONE",3);
				seqMap.put("WEBSITE",4);
				//seqMap.put("INDUSTRY",5);
				seqMap.put("TAGMODULEREFID",5);
				seqMap.put("DESCRIPTION",6);
				seqMap.put("SMCREATORID",17);
				seqMap.put("MODIFIEDBY",18);
				seqMap.put("CREATEDTIME",19);
				seqMap.put("MODIFIEDTIME",20);
				seqMap.put("LASTACTIVITYTIME",22);
				seqMap.put("SE_STATUS",21);
				seqMap.put("PHOTO_FILEID",1);
				
				seqMap.put("BILLINGSTREET",1);
				//seqMap.put("SHIPPINGSTREET",2);
				seqMap.put("BILLINGCITY",2);
				//seqMap.put("SHIPPINGCITY",4);
				seqMap.put("BILLINGSTATE",3);
				//seqMap.put("SHIPPINGSTATE",6);
				seqMap.put("BILLINGCOUNTRY",4);
				//seqMap.put("SHIPPINGCOUNTRY",8);
				seqMap.put("BILLINGCODE",5);
				//seqMap.put("SHIPPINGCODE",10);
				HashMap fieldMap=new HashMap();
				if(fielddObj!=null && !fielddObj.isEmpty()){
					ArrayList idArray=new ArrayList();
					Iterator fieldItr=fielddObj.getRows(CRMFIELD.TABLE);
					while(fieldItr.hasNext()){
						Row fieldRow=(Row) fieldItr.next();
						if(seqMap.containsKey(fieldRow.get(CRMFIELD.COLUMNNAME).toString())){
							idArray.add(fieldRow.get(CRMFIELD.FIELDID));
							fieldMap.put(Long.parseLong(fieldRow.get(CRMFIELD.FIELDID).toString()), fieldRow.get(CRMFIELD.COLUMNNAME).toString());
						}
					}
					Criteria cr=new Criteria(Column.getColumn(CRMMODEFIELD.TABLE, CRMMODEFIELD.FIELDID),idArray.toArray(),QueryConstants.NOT_IN);
					cr=cr.and(new Criteria(Column.getColumn(CRMMODEFIELD.TABLE, CRMMODEFIELD.LAYOUTID),layoutId,QueryConstants.EQUAL));
					per.delete(cr);
					//DataObject modfieldObj=CrmFieldUtil.getModeFieldsDOForModule(CrmModuleConstants.ACTIVITIES, zgid);
					SelectQuery sQry = new SelectQueryImpl(Table.getTable(CRMMODEFIELD.TABLE));
					sQry.addSelectColumn( Column.getColumn(null,"*") );
					Criteria modcr=new Criteria(Column.getColumn(CRMMODEFIELD.TABLE, CRMMODEFIELD.TABID), tabId , QueryConstants.EQUAL);
					sQry.setCriteria(modcr);
					DataObject modfieldObj = per.get(sQry);
					if(modfieldObj!=null && !modfieldObj.isEmpty()){
						Iterator modItr=modfieldObj.getRows(CRMMODEFIELD.TABLE);
						while (modItr.hasNext()){
							Row modRow=(Row) modItr.next();	
							Long fieldId=Long.parseLong(modRow.get(CRMMODEFIELD.FIELDID).toString());
							if(fieldMap.containsKey(fieldId)){
								String columnName=(String) fieldMap.get(fieldId);
								modRow.set(CRMMODEFIELD.FIELDSEQUENCE, seqMap.get(columnName));
								if(columnName!=null && (columnName.indexOf("BILLING")!=-1 || columnName.indexOf("SHIPPING")!=-1)){
									modRow.set(CRMMODEFIELD.SECTIONID, 2);
									modRow.set(CRMMODEFIELD.SECTION_ID, section_IdForSection2);
								}else if(columnName!=null && columnName.indexOf("PHOTO_FILEID")!=-1){
									modRow.set(CRMMODEFIELD.SECTIONID, 5);
									modRow.set(CRMMODEFIELD.SECTION_ID, section_IdForSection5);
								}else{
									modRow.set(CRMMODEFIELD.SECTIONID, 1);
									modRow.set(CRMMODEFIELD.SECTION_ID, section_IdForSection1);
								}
								modfieldObj.updateRow(modRow);
							}
						}
						per.update(modfieldObj);
					}
					
					//Section creation for additional information fields
					createSection(zgid, tabId,layoutId,3,"Additional Information",2);//No I18N
					
					//Update related list for accounts
					HashMap listMap=new HashMap();
					listMap.put("NOTESPERSONALITY", 1);
					listMap.put("ACTIVITYPERSONALITY", 2);
					listMap.put("ACTIVITYHISTORYPERSONALITY", 10);
					listMap.put("EMAILSPERSONALITY", 3);
					listMap.put("SOCIALPERSONALITY", 4);
					listMap.put("POTENTIALSPERSONALITY", 5);
					listMap.put("CONTACTSPERSONALITY", 6);
					listMap.put("ATTACHMENTSPERSONALITY", 7);
					
					RelatedListUtil.updateRelatedList(listMap,Long.parseLong(tabId));
			    }
			}
		}catch(Exception e){
			LOGGER.log(Level.SEVERE, "Exception occurred while editing default accounts layout for Ignite user ::"+e);
			throw e;
		}
	}
	public void modifyContactLayout(String zgid) throws Exception{
		
		//String zgid=""+request.getAttribute( CrmConstants.ZGID_FROM_FILTER);
		try{
			Persistence per = PersistenceCacheUtil.getPersistenceLiteHandle(zgid);
			DataObject fielddObj=FieldUtil.getFieldsDOForModule(CrmModuleConstants.CONTACTS, zgid);
			if(fielddObj!=null && !fielddObj.isEmpty()){
				String tabId=""+fielddObj.getRow(CRMFIELD.TABLE).get(CRMFIELD.TABID);
				Long layoutId=PageLayoutUtil.getSystemdefinedActiveLayout(Long.parseLong(tabId),zgid);
				HashMap<String, Long> sectionPKVsSection_ID = SectionUtil.getSectionPKVsSection_IDMap(Long.valueOf(tabId), layoutId, null, zgid);
				Long section_IdForSection1 = null;
				Long section_IdForSection2 = null;
				Long section_IdForSection5 = null;
				if(sectionPKVsSection_ID != null){
					String pkKey = new StringBuilder().append(layoutId).append('_').append(CrmConstants.LAYOUT_DEFAULTMODE).append('_').append(1L).toString();
					section_IdForSection1 = sectionPKVsSection_ID.get(pkKey);
					pkKey = new StringBuilder().append(layoutId).append('_').append(CrmConstants.LAYOUT_DEFAULTMODE).append('_').append(2L).toString();
					section_IdForSection2 = sectionPKVsSection_ID.get(pkKey);
					pkKey = new StringBuilder().append(layoutId).append('_').append(CrmConstants.LAYOUT_DEFAULTMODE).append('_').append(5L).toString();
					section_IdForSection5 = sectionPKVsSection_ID.get(pkKey);
				}
				HashMap seqMap =new HashMap();
				
				//Ignite default Contacts columns 
				seqMap.put("FIRSTNAME",1);
				seqMap.put("LASTNAME",2);
				seqMap.put("TITLE",3);
				seqMap.put("EMAIL",4);
				seqMap.put("ACCOUNTID",5);
				seqMap.put("MOBILE",6);
				seqMap.put("PHONE",7); // Work Phone 
				seqMap.put("HOMEPHONE",8);
				seqMap.put("SMOWNERID",9);
				seqMap.put("EMAILOPTOUT",10);
				seqMap.put("TAGMODULEREFID",11);
				seqMap.put("DESCRIPTION",12);
				seqMap.put("FULLNAME",27);
				seqMap.put("SMCREATORID",21);
				seqMap.put("MODIFIEDBY",23);
				seqMap.put("CREATEDTIME",25);
				seqMap.put("MODIFIEDTIME",26);
				seqMap.put("LASTACTIVITYTIME",33);
				seqMap.put("SE_STATUS",28);
				seqMap.put("PHOTO_FILEID",1);
				
				
				seqMap.put("MAILINGSTREET","1");
				seqMap.put("MAILINGCITY","2");
				seqMap.put("MAILINGSTATE","3");
				seqMap.put("MAILINGCOUNTRY","4");
				seqMap.put("MAILINGZIP","5");
				HashMap fieldMap=new HashMap();
				if(fielddObj!=null && !fielddObj.isEmpty()){
					ArrayList idArray=new ArrayList();
					Iterator fieldItr=fielddObj.getRows(CRMFIELD.TABLE);
					while(fieldItr.hasNext()){
						Row fieldRow=(Row) fieldItr.next();
						if(seqMap.containsKey(fieldRow.get(CRMFIELD.COLUMNNAME).toString())){
							idArray.add(fieldRow.get(CRMFIELD.FIELDID));
							fieldMap.put(Long.parseLong(fieldRow.get(CRMFIELD.FIELDID).toString()), fieldRow.get(CRMFIELD.COLUMNNAME).toString());
						}
					}
					Criteria cr=new Criteria(Column.getColumn(CRMMODEFIELD.TABLE, CRMMODEFIELD.FIELDID),idArray.toArray(),QueryConstants.NOT_IN);
					cr=cr.and(new Criteria(Column.getColumn(CRMMODEFIELD.TABLE, CRMMODEFIELD.LAYOUTID),layoutId,QueryConstants.EQUAL));
					per.delete(cr);
					//DataObject modfieldObj=CrmFieldUtil.getModeFieldsDOForModule(CrmModuleConstants.ACTIVITIES, zgid);
					SelectQuery sQry = new SelectQueryImpl(Table.getTable(CRMMODEFIELD.TABLE));
					sQry.addSelectColumn( Column.getColumn(null,"*") );
					Criteria modcr=new Criteria(Column.getColumn(CRMMODEFIELD.TABLE, CRMMODEFIELD.TABID), tabId , QueryConstants.EQUAL);
					sQry.setCriteria(modcr);
					DataObject modfieldObj = per.get(sQry);
					if(modfieldObj!=null && !modfieldObj.isEmpty()){
						Iterator modItr=modfieldObj.getRows(CRMMODEFIELD.TABLE);
						while (modItr.hasNext()){
							Row modRow=(Row) modItr.next();	
							Long fieldId=Long.parseLong(modRow.get(CRMMODEFIELD.FIELDID).toString());
							if(fieldMap.containsKey(fieldId)){
								String columnName=(String) fieldMap.get(fieldId);
								modRow.set(CRMMODEFIELD.FIELDSEQUENCE, seqMap.get(columnName));
								if(columnName!=null && columnName.indexOf("MAILING")!=-1){
									modRow.set(CRMMODEFIELD.SECTIONID, 2);
									modRow.set(CRMMODEFIELD.SECTION_ID, section_IdForSection2);
								}else if(columnName!=null && columnName.indexOf("PHOTO_FILEID")!=-1){
									modRow.set(CRMMODEFIELD.SECTIONID, 5);
									modRow.set(CRMMODEFIELD.SECTION_ID, section_IdForSection5);
								}else{
									modRow.set(CRMMODEFIELD.SECTIONID, 1);
									modRow.set(CRMMODEFIELD.SECTION_ID, section_IdForSection1);
								}
								modfieldObj.updateRow(modRow);
							}
						}
						per.update(modfieldObj);
					}
					
					//Section creation for additional information fields
					createSection(zgid, tabId,layoutId,3,"Additional Information",2);//No I18N
					
					//Update related list for contacts
					HashMap listMap=new HashMap();
					listMap.put("NOTESPERSONALITY", 1);
					listMap.put("ACTIVITYPERSONALITY", 2);
					listMap.put("ACTIVITYHISTORYPERSONALITY", 10);
					listMap.put("EMAILSPERSONALITY", 3);
					listMap.put("SOCIALPERSONALITY", 4);
					listMap.put("POTENTIALSPERSONALITY", 5);
					listMap.put("ATTACHMENTSPERSONALITY", 6);
					
					RelatedListUtil.updateRelatedList(listMap,Long.parseLong(tabId));
				}
			}
		}catch(Exception e){
			
			LOGGER.log(Level.SEVERE, "Exception occurred while editing default contacts layout for Ignite user ::"+e);
			throw e;
		}
	}

/*private void modifyLeadLayout(HttpServletRequest request,HttpServletResponse response){
	
	//String zgid=""+request.getAttribute( CrmConstants.ZGID_FROM_FILTER);
	JSONObject layoutJson=new JSONObject();
	JSONObject layoutObj=new JSONObject();
	try{
		
		DataObject fielddObj=CrmFieldUtil.getFieldsDOForModule(CrmModuleConstants.LEADS, zgid);
		if(fielddObj!=null && !fielddObj.isEmpty()){
			String tabId=""+fielddObj.getRow(CRMFIELD.TABLE).get(CRMFIELD.TABID);
			Long layoutId=PageLayoutUtil.getSystemdefinedActiveLayout(Long.parseLong(tabId),zgid);
		
			layoutJson.put("tabId", tabId);
			layoutJson.put("layoutId", layoutId);
			layoutObj.put("layoutInfo", layoutJson);
			
			JSONObject extraJson=new JSONObject();
			JSONObject completeMBJson=new JSONObject();
			JSONObject fieldJson=new JSONObject();
			HashMap<String, String> fieldMap=new HashMap();
			HashMap idMap=new HashMap();
			HashMap listMap=new HashMap();
			HashMap seqMap=new HashMap();
			HashMap delMap=new HashMap();
			
			//Ignite default Contact columns
			String columnList[]={"FIRSTNAME","LASTNAME","COMPANY","DESIGNATION","EMAIL","MOBILE","PHONE","WEBSITE","SMOWNERID","LEADSOURCE","STATUS","EMAILOPTOUT","DESCRIPTION","LANE","CITY","STATE","COUNTRY","CODE","FAX","EMPCT","RATING","SKYPEIDENTITY","ADDN_EMAIL","TWITTER","ANNUALREVENUE","INDUSTRY"};//No I18N
			
			//Ignite Default Contact columns
			listMap.put("FIRSTNAME","1");
			listMap.put("LASTNAME","2");
			listMap.put("COMPANY","3");
			listMap.put("DESIGNATION","4");
			listMap.put("EMAIL","5");
			listMap.put("MOBILE","6");
			listMap.put("PHONE","7"); 
			listMap.put("WEBSITE","8");
			listMap.put("SMOWNERID","9");
			listMap.put("LEADSOURCE","10");
			listMap.put("STATUS","11");
			listMap.put("EMAILOPTOUT","12");
			listMap.put("DESCRIPTION","13");
			
			listMap.put("LANE","1");
			listMap.put("CITY","2");
			listMap.put("STATE","3");
			listMap.put("COUNTRY","4");
			listMap.put("CODE","5");
			
			//Ignite Contact columns to be deleted
			delMap.put("FAX","14");
			delMap.put("EMPCT","15");
			delMap.put("RATING","16");
			delMap.put("SKYPEIDENTITY","17");
			delMap.put("ADDN_EMAIL","18");
			delMap.put("TWITTER","19");
			delMap.put("ANNUALREVENUE","20");
			delMap.put("INDUSTRY","21");
			
			// Sequence
			seqMap.put("FAX",9);
			seqMap.put("EMPCT",10);
			seqMap.put("RATING",11);
			seqMap.put("SKYPEIDENTITY",13);
			seqMap.put("ADDN_EMAIL",16);
			seqMap.put("TWITTER",15);
			seqMap.put("ANNUALREVENUE",17);
			seqMap.put("INDUSTRY",18);
			
			Criteria cr=new Criteria(Column.getColumn(CRMFIELD.TABLE, CRMFIELD.COLUMNNAME),columnList,QueryConstants.IN);
			Iterator<Row> fieldItr=fielddObj.getRows(CRMFIELD.TABLE,cr);
			int seqcount=0;
			int[] uitype = new int[delMap.size()];
			int[] fseq=new int[delMap.size()];
			while(fieldItr.hasNext()){
				Row fieldRow=fieldItr.next();
				if(listMap.containsKey(""+fieldRow.get(CRMFIELD.COLUMNNAME))){
					fieldMap.put(""+fieldRow.get(CRMFIELD.FIELDID), listMap.get(""+fieldRow.get(CRMFIELD.COLUMNNAME)).toString());
				}else if(delMap.containsKey(""+fieldRow.get(CRMFIELD.COLUMNNAME))){
					idMap.put("F"+delMap.get(""+fieldRow.get(CRMFIELD.COLUMNNAME)), Long.parseLong(""+fieldRow.get(CRMFIELD.FIELDID)));
					uitype[seqcount]=Integer.parseInt(""+fieldRow.get(CRMFIELD.UITYPE));
					fseq[seqcount]=(int)seqMap.get(""+fieldRow.get(CRMFIELD.COLUMNNAME));
					seqcount++;
				}
			}
			
			String addressBlock = "LANE,CITY,STATE,COUNTRY,CODE"; //No I18N
			
			//Constructing modify pagelayout json for ignite Contacts module
			Iterator<Map.Entry<String,String>> it = fieldMap.entrySet().iterator();
		    while (it.hasNext()) {
		        Map.Entry fieldPair = (Map.Entry)it.next();
		        JSONObject fieldseqJson=new JSONObject();
		        String colName = fielddObj.getRow(CRMFIELD.TABLE, new Criteria(Column.getColumn(CRMFIELD.TABLE, CRMFIELD.FIELDID),fieldPair.getKey().toString(),QueryConstants.EQUAL)).get(CRMFIELD.COLUMNNAME).toString(); 
		        if(addressBlock.contains(colName)){ // for section or block handling
		        	
		        	fieldseqJson.put("par", "2");
		        }else{
		        	
		        	fieldseqJson.put("par", "1");
		        }
		        
		        
		        fieldseqJson.put("seq", fieldPair.getValue());
		        fieldJson.put(fieldPair.getKey().toString(), fieldseqJson);
		    }
		    String fieldtype[]={"textField","integerField","picklistField","textField","emailField","textField","currencyField","picklistField"};//No I18N
		    HashMap nameMap=new HashMap();
		    
		    nameMap.put("Fax", "Fax");
		    nameMap.put("No_of_Employees", "No of Employees");
		    nameMap.put("Rating","Rating");
		    nameMap.put("Skype_ID","Skype ID");
		    nameMap.put("Secondary_Email", "Secondary Email");
		    nameMap.put("Twitter", "Twitter");
		    nameMap.put("Annual_Revenue", "Annual Revenue");
		    nameMap.put("Industry", "Industry");
		    
		    String fname[]={"FAX","EMPCT","RATING","SKYPEIDENTITY","ADDN_EMAIL","TWITTER","ANNUALREVENUE","INDUSTRY"}; //No I18N
		    
		    JSONObject fieldObj = getDelFieldObj(layoutId, nameMap, fieldtype, idMap, fname, uitype, fseq);
		    completeMBJson.put("fDtls", fieldObj);
		    completeMBJson.put("fSeq", fieldJson);
		    completeMBJson.put("extraDomValues", extraJson);
		    layoutObj.put("completeMBInfo", completeMBJson);
		    sendReqForLayoutModification(CrmModuleConstants.LEADS, layoutObj);
		}      
	}catch(Exception e){
		
		LOGGER.log(Level.SEVERE, "Exception occurred while editing default Lead layout for Ignite user ::"+e);
	}

}
*/
	public void updateDealKanbanColumns(String fieldId,String tabId,String zgid) throws Exception{
		try{
			DataObject configObj=CrmViewConfigUtil.getDataObject(Long.parseLong(tabId), CrmViewConfigUtil.FeatureType.KANBANVIEWSETUP.getType());
			if(configObj!=null && !configObj.isEmpty()){
				Row configRow=configObj.getRow(CRMVIEWSETTINGS.TABLE);
				String configprops=configRow.get(CRMVIEWSETTINGS.CONFIGPROPS).toString();
				JSONObject propJson=new JSONObject(configprops);
				JSONArray columnArr=propJson.getJSONArray("selectColumns");//No I18N
				JSONArray newcolumnArr=new JSONArray();
				int colLen=columnArr.length();
				for(int count=0;count<colLen;count++){
					if(!fieldId.equalsIgnoreCase(columnArr.get(count).toString())){
						if(colLen>2){
							if(count==1){
								newcolumnArr.put(columnArr.get(2).toString());
							}else if(count==2){
								newcolumnArr.put(columnArr.get(1).toString());
							}else{
								newcolumnArr.put(columnArr.get(count).toString());
							}
						}else{
							newcolumnArr.put(columnArr.get(count).toString());
						}
					}
				}
				propJson.remove("selectColumns");
				propJson.put("selectColumns", newcolumnArr);
				configRow.set(CRMVIEWSETTINGS.CONFIGPROPS, propJson.toString());
				configObj.updateRow(configRow);
				PersistenceCacheUtil.getNonCachePersistenceHandle(zgid).update(configObj);
			}
		}catch(Exception e){
			LOGGER.log(Level.SEVERE,"Exception while updating Deals kanban view columns ::"+e);
			throw e;
		}
	}
	
	public void modifyDealLayout(String zgid) throws Exception{
		try{
			Persistence per = PersistenceCacheUtil.getPersistenceLiteHandle(zgid);
			DataObject fielddObj=FieldUtil.getFieldsDOForModule(CrmModuleConstants.POTENTIALS, zgid);
			if(fielddObj!=null && !fielddObj.isEmpty()){
				String tabId=""+fielddObj.getRow(CRMFIELD.TABLE).get(CRMFIELD.TABID);
				Long layoutId=PageLayoutUtil.getSystemdefinedActiveLayout(Long.parseLong(tabId),zgid);
				HashMap<String, Long> sectionPKVsSection_ID = SectionUtil.getSectionPKVsSection_IDMap(Long.valueOf(tabId), layoutId, null, zgid);
				Long section_IdForSection1 = null;
				Long section_IdForSection2 = null;
				if(sectionPKVsSection_ID != null){
					String pkKey = new StringBuilder().append(layoutId).append('_').append(CrmConstants.LAYOUT_DEFAULTMODE).append('_').append(1L).toString();
					section_IdForSection1 = sectionPKVsSection_ID.get(pkKey);
					pkKey = new StringBuilder().append(layoutId).append('_').append(CrmConstants.LAYOUT_DEFAULTMODE).append('_').append(2L).toString();
					section_IdForSection2 = sectionPKVsSection_ID.get(pkKey);
				}
				HashMap seqMap =new HashMap();
				
				//Ignite Default deal columns
				seqMap.put("POTENTIALNAME",1);
				seqMap.put("SMOWNERID",2);
				seqMap.put("ACCOUNTID",3);
				seqMap.put("CONTACTID",4);
				seqMap.put("PIPELINE",5);
				seqMap.put("STAGE",6);
				seqMap.put("AMOUNT",7);
				seqMap.put("CLOSINGDATE",8);
				seqMap.put("TAGMODULEREFID",9);
				seqMap.put("DESCRIPTION",10);
				seqMap.put("SMCREATORID",14);
				seqMap.put("MODIFIEDBY",15);
				seqMap.put("CREATEDTIME",16);
				seqMap.put("MODIFIEDTIME",17);
				seqMap.put("LASTACTIVITYTIME",21);
				
				HashMap fieldMap=new HashMap();
				if(fielddObj!=null && !fielddObj.isEmpty()){
					ArrayList idArray=new ArrayList();
					Iterator fieldItr=fielddObj.getRows(CRMFIELD.TABLE);
					while(fieldItr.hasNext()){
						Row fieldRow=(Row) fieldItr.next();
						if(seqMap.containsKey(fieldRow.get(CRMFIELD.COLUMNNAME).toString())){
							idArray.add(fieldRow.get(CRMFIELD.FIELDID));
							fieldMap.put(Long.parseLong(fieldRow.get(CRMFIELD.FIELDID).toString()), fieldRow.get(CRMFIELD.COLUMNNAME).toString());
							if(fieldRow.get(CRMFIELD.COLUMNNAME).toString().equalsIgnoreCase("CONTACTID")){
								updateDealKanbanColumns(fieldRow.get(CRMFIELD.FIELDID).toString(),tabId,zgid);
							}
							if(fieldRow.get(CRMFIELD.COLUMNNAME).toString().equalsIgnoreCase("STAGE")){
								//Update picklistvalues for deal stage - removed closed lost to competition
								Criteria cr=new Criteria(Column.getColumn(CRMOPPORTUNITYSTAGE.TABLE, CRMOPPORTUNITYSTAGE.ACTUALSTAGE),"Closed Lost to Competition",QueryConstants.EQUAL);
								cr=cr.or(new Criteria(Column.getColumn(CRMOPPORTUNITYSTAGE.TABLE, CRMOPPORTUNITYSTAGE.ACTUALSTAGE),"Value Proposition",QueryConstants.EQUAL));
								cr=cr.or(new Criteria(Column.getColumn(CRMOPPORTUNITYSTAGE.TABLE, CRMOPPORTUNITYSTAGE.ACTUALSTAGE),"Id. Decision Makers",QueryConstants.EQUAL));
								per.delete(cr);
							}
						}
					}
					Criteria cr=new Criteria(Column.getColumn(CRMMODEFIELD.TABLE, CRMMODEFIELD.FIELDID),idArray.toArray(),QueryConstants.NOT_IN);
					cr=cr.and(new Criteria(Column.getColumn(CRMMODEFIELD.TABLE, CRMMODEFIELD.LAYOUTID),layoutId,QueryConstants.EQUAL));
					per.delete(cr);
					//DataObject modfieldObj=CrmFieldUtil.getModeFieldsDOForModule(CrmModuleConstants.ACTIVITIES, zgid);
					SelectQuery sQry = new SelectQueryImpl(Table.getTable(CRMMODEFIELD.TABLE));
					sQry.addSelectColumn( Column.getColumn(null,"*") );
					Criteria modcr=new Criteria(Column.getColumn(CRMMODEFIELD.TABLE, CRMMODEFIELD.TABID), tabId , QueryConstants.EQUAL);
					sQry.setCriteria(modcr);
					DataObject modfieldObj = per.get(sQry);
					if(modfieldObj!=null && !modfieldObj.isEmpty()){
						Iterator modItr=modfieldObj.getRows(CRMMODEFIELD.TABLE);
						while (modItr.hasNext()){
							Row modRow=(Row) modItr.next();	
							Long fieldId=Long.parseLong(modRow.get(CRMMODEFIELD.FIELDID).toString());
							if(fieldMap.containsKey(fieldId)){
								String columnName=(String) fieldMap.get(fieldId);
								modRow.set(CRMMODEFIELD.FIELDSEQUENCE, seqMap.get(columnName));
								if(columnName!=null && columnName.indexOf("MAILING")!=-1){
									modRow.set(CRMMODEFIELD.SECTIONID, 2);
									modRow.set(CRMMODEFIELD.SECTION_ID, section_IdForSection2);
								}else{
									modRow.set(CRMMODEFIELD.SECTIONID, 1);
									modRow.set(CRMMODEFIELD.SECTION_ID, section_IdForSection1);
								}
								modfieldObj.updateRow(modRow);
							}
						}
						per.update(modfieldObj);
					}
					
					//Section creation for additional information fields
					createSection(zgid, tabId,layoutId,2,"Additional Information",2);//No I18N
					
					//Update related list for deals
					HashMap listMap=new HashMap();
					listMap.put("NOTESPERSONALITY", 1);
					listMap.put("ACTIVITYPERSONALITY", 2);
					listMap.put("ACTIVITYHISTORYPERSONALITY", 10);
					listMap.put("EMAILSPERSONALITY", 3);
					listMap.put("POTSTAGEHISTPERSONALITY",4);
					listMap.put("ATTACHMENTSPERSONALITY", 5);
					listMap.put("PRODUCTSPERSONALITY", 6);
					
					RelatedListUtil.updateRelatedList(listMap,Long.parseLong(tabId));
				}
			}
		}catch(Exception e){
			
			LOGGER.log(Level.SEVERE, "Exception occurred while editing default deal layout for Ignite user ::"+e);
			throw e;
		}
		try{
			markKanbanViewAsDefault(zgid);
		}catch(Exception ex){
			LOGGER.log(Level.SEVERE, "Exception occurred while marking kandan view as default in deals for Ignite user ::"+ex);
			throw ex;
		}
	}
	
	public void markKanbanViewAsDefault(String zgid) throws Exception{
		UpdateQuery updQry = new UpdateQueryImpl( CRMDEFAULTCV.TABLE);
		Criteria crt=new Criteria( Column.getColumn( CRMDEFAULTCV.TABLE, CRMDEFAULTCV.MODULE),CrmModuleConstants.POTENTIALS, QueryConstants.EQUAL);
		//crt=crt.and(new Criteria( Column.getColumn( CRMDEFAULTCV.TABLE, CRMDEFAULTCV.USERID),userid, QueryConstants.EQUAL));
		updQry.setCriteria(crt);
		updQry.setUpdateColumn(CRMDEFAULTCV.KANBANVIEW, 1);
		try {
			PersistenceCacheUtil.getPersistenceLiteHandle(zgid).update( updQry);
		}catch (Exception e) {
			LOGGER.log(Level.WARNING,"Exception Occured while updating View in CRMDEFAULTCV table for bigin user ::"+e);
			throw e;
		}
	}

	public void modifyTaskLayout(String zgid) throws Exception{
		
		//{"layoutInfo":{"tabId":"1000000000063","layoutId":"1000000000199"},"completeMBInfo":{"fSeq":{"1000000000335":{"par":"1","seq":1},"1000000000373":{"par":"1","seq":3},"1000000000341":{"par":"1","seq":5},"1000000000333":{"par":"1","seq":7},"1000000000345":{"par":"1","seq":9},"1000000000343":{"par":"1","seq":11},"1000000000337":{"par":"1","seq":13},"1000000000351":{"par":"1","seq":15},"1000000000347":{"par":"1","seq":17}},"fDtls":{"F10":{"isDisplayColumn":false,"sys_ref":"Who_Id","label":"Contact Name","showtype":7,"uitype":4,"osid":1,"source":1,"gentype":1,"fSeq":7,"name":"CONTACTID","etype":"lookupField","layouts_id":["1000000000199"],"id":"1000000000339","presence":0,"dblabel":"Who Id","pdeid":"S0","deid":"F10"},"F11":{"isDisplayColumn":false,"sys_ref":"Created_By","label":"Created By","showtype":7,"uitype":20,"osid":1,"source":1,"mxlen":25,"gentype":1,"fSeq":16,"name":"SMCREATORID","etype":"textField","layouts_id":["1000000000199"],"id":"1000000000355","presence":0,"dblabel":"Created By","pdeid":"S0","deid":"F11"},"F12":{"isDisplayColumn":false,"sys_ref":"Modified_By","label":"Modified By","showtype":7,"uitype":20,"osid":1,"source":1,"mxlen":25,"gentype":1,"fSeq":18,"name":"MODIFIEDBY","etype":"textField","layouts_id":["1000000000199"],"id":"1000000000357","presence":0,"dblabel":"Modified By","pdeid":"S0","deid":"F12"},"F13":{"isDisplayColumn":false,"sys_ref":"Send_Notification_Email","label":"Send Notification Email","showtype":7,"uitype":301,"osid":1,"source":1,"defenbl":false,"gentype":1,"fSeq":17,"name":"SENDNOTIFICATION","etype":"decisionboxField","layouts_id":["1000000000199"],"id":"1000000000349","presence":0,"dblabel":"Send Notification Email","pdeid":"S0","deid":"F13"}},"delSIds":[2],"extraDomValues":{}}}
		try{
			Persistence per = PersistenceCacheUtil.getPersistenceLiteHandle(zgid);
			DataObject fielddObj=FieldUtil.getFieldsDOForModule(CrmModuleConstants.TASKS, zgid);
			
			if(fielddObj!=null && !fielddObj.isEmpty()){
				String tabId=""+fielddObj.getRow(CRMFIELD.TABLE).get(CRMFIELD.TABID);
				Long layoutId=PageLayoutUtil.getSystemdefinedActiveLayout(Long.parseLong(tabId),zgid);
				HashMap<String, Long> sectionPKVsSection_ID = SectionUtil.getSectionPKVsSection_IDMap(Long.valueOf(tabId), layoutId, null, zgid);
				Long section_IdForSection1 = null;
				if(sectionPKVsSection_ID != null){
					String pkKey = new StringBuilder().append(layoutId).append('_').append(CrmConstants.LAYOUT_DEFAULTMODE).append('_').append(1L).toString();
					section_IdForSection1 = sectionPKVsSection_ID.get(pkKey);
				}
				HashMap seqMap =new HashMap();
				
				//Ignite default Task columns
				seqMap.put("SUBJECT",1);
				seqMap.put("SEID",2);
				seqMap.put("CONTACTID", 2);
				seqMap.put("SMOWNERID",3);
				seqMap.put("PRIORITY",4);
				seqMap.put("STATUS",5);
				seqMap.put("DUEDATE",6);
				seqMap.put("RECURRING",7);
				seqMap.put("REMINDAT",8);
				seqMap.put("DESCRIPTION", 9);
				seqMap.put("SMCREATORID",16);
				seqMap.put("MODIFIEDBY",18);
				seqMap.put("CREATEDTIME",21);
				seqMap.put("MODIFIEDTIME",25);
				seqMap.put("TAGMODULEREFID",26);
				seqMap.put("CLOSEDTIME",27);
				HashMap fieldMap=new HashMap();
				if(fielddObj!=null && !fielddObj.isEmpty()){
					ArrayList idArray=new ArrayList();
					Iterator fieldItr=fielddObj.getRows(CRMFIELD.TABLE);
					while(fieldItr.hasNext()){
						Row fieldRow=(Row) fieldItr.next();
						if(seqMap.containsKey(fieldRow.get(CRMFIELD.COLUMNNAME).toString())){
							idArray.add(fieldRow.get(CRMFIELD.FIELDID));
							fieldMap.put(Long.parseLong(fieldRow.get(CRMFIELD.FIELDID).toString()), fieldRow.get(CRMFIELD.COLUMNNAME).toString());
							if(fieldRow.get(CRMFIELD.COLUMNNAME).toString().equalsIgnoreCase("PRIORITY") || fieldRow.get(CRMFIELD.COLUMNNAME).toString().equalsIgnoreCase("STATUS")){
								//Update picklistvalues for task priority and status
								updatePicklistValues(fieldRow.get(CRMFIELD.FIELDID).toString(),layoutId,fieldRow.get(CRMFIELD.COLUMNNAME).toString());
							}
						}
					}
					Criteria cr=new Criteria(Column.getColumn(CRMMODEFIELD.TABLE, CRMMODEFIELD.FIELDID),idArray.toArray(),QueryConstants.NOT_IN);
					cr=cr.and(new Criteria(Column.getColumn(CRMMODEFIELD.TABLE, CRMMODEFIELD.LAYOUTID),layoutId,QueryConstants.EQUAL));
					per.delete(cr);
					//DataObject modfieldObj=CrmFieldUtil.getModeFieldsDOForModule(CrmModuleConstants.ACTIVITIES, zgid);
					SelectQuery sQry = new SelectQueryImpl(Table.getTable(CRMMODEFIELD.TABLE));
					sQry.addSelectColumn( Column.getColumn(null,"*") );
					Criteria modcr=new Criteria(Column.getColumn(CRMMODEFIELD.TABLE, CRMMODEFIELD.TABID), tabId , QueryConstants.EQUAL);
					sQry.setCriteria(modcr);
					DataObject modfieldObj = per.get(sQry);
					if(modfieldObj!=null && !modfieldObj.isEmpty()){
						Iterator modItr=modfieldObj.getRows(CRMMODEFIELD.TABLE);
						while (modItr.hasNext()){
							Row modRow=(Row) modItr.next();	
							Long fieldId=Long.parseLong(modRow.get(CRMMODEFIELD.FIELDID).toString());
							if(fieldMap.containsKey(fieldId)){
								String columnName=(String) fieldMap.get(fieldId);
								modRow.set(CRMMODEFIELD.FIELDSEQUENCE, seqMap.get(columnName));
								modRow.set(CRMMODEFIELD.SECTIONID, 1);
								modRow.set(CRMMODEFIELD.SECTION_ID, section_IdForSection1);
								modfieldObj.updateRow(modRow);
							}
						}
						per.update(modfieldObj);
					}
				}
			}
		}catch(Exception e){
			
			LOGGER.log(Level.SEVERE, "Exception occurred while editing default tasks layout for Ignite user ::"+e);
			throw e;
		}
	}
	
	public void modifyEventLayout(String zgid) throws Exception{
		
		//{"layoutInfo":{"tabId":"1000000000063","layoutId":"1000000000199"},"completeMBInfo":{"fSeq":{"1000000000335":{"par":"1","seq":1},"1000000000373":{"par":"1","seq":3},"1000000000341":{"par":"1","seq":5},"1000000000333":{"par":"1","seq":7},"1000000000345":{"par":"1","seq":9},"1000000000343":{"par":"1","seq":11},"1000000000337":{"par":"1","seq":13},"1000000000351":{"par":"1","seq":15},"1000000000347":{"par":"1","seq":17}},"fDtls":{"F10":{"isDisplayColumn":false,"sys_ref":"Who_Id","label":"Contact Name","showtype":7,"uitype":4,"osid":1,"source":1,"gentype":1,"fSeq":7,"name":"CONTACTID","etype":"lookupField","layouts_id":["1000000000199"],"id":"1000000000339","presence":0,"dblabel":"Who Id","pdeid":"S0","deid":"F10"},"F11":{"isDisplayColumn":false,"sys_ref":"Created_By","label":"Created By","showtype":7,"uitype":20,"osid":1,"source":1,"mxlen":25,"gentype":1,"fSeq":16,"name":"SMCREATORID","etype":"textField","layouts_id":["1000000000199"],"id":"1000000000355","presence":0,"dblabel":"Created By","pdeid":"S0","deid":"F11"},"F12":{"isDisplayColumn":false,"sys_ref":"Modified_By","label":"Modified By","showtype":7,"uitype":20,"osid":1,"source":1,"mxlen":25,"gentype":1,"fSeq":18,"name":"MODIFIEDBY","etype":"textField","layouts_id":["1000000000199"],"id":"1000000000357","presence":0,"dblabel":"Modified By","pdeid":"S0","deid":"F12"},"F13":{"isDisplayColumn":false,"sys_ref":"Send_Notification_Email","label":"Send Notification Email","showtype":7,"uitype":301,"osid":1,"source":1,"defenbl":false,"gentype":1,"fSeq":17,"name":"SENDNOTIFICATION","etype":"decisionboxField","layouts_id":["1000000000199"],"id":"1000000000349","presence":0,"dblabel":"Send Notification Email","pdeid":"S0","deid":"F13"}},"delSIds":[2],"extraDomValues":{}}}
		try{
			Persistence per = PersistenceCacheUtil.getPersistenceLiteHandle(zgid);
			DataObject fielddObj=FieldUtil.getFieldsDOForModule(CrmModuleConstants.EVENTS, zgid);
			if(fielddObj!=null && !fielddObj.isEmpty()){
				String tabId=""+fielddObj.getRow(CRMFIELD.TABLE).get(CRMFIELD.TABID);
				Long layoutId=PageLayoutUtil.getSystemdefinedActiveLayout(Long.parseLong(tabId),zgid);
				HashMap<String, Long> sectionPKVsSection_ID = SectionUtil.getSectionPKVsSection_IDMap(Long.valueOf(tabId), layoutId, null, zgid);
				Long section_IdForSection1 = null;
				Long section_IdForSection4 = null;
				Long section_IdForSection3 = null;
				if(sectionPKVsSection_ID != null){
					String pkKey = new StringBuilder().append(layoutId).append('_').append(1L).append('_').append(1L).toString();
					section_IdForSection1 = sectionPKVsSection_ID.get(pkKey);
					pkKey = new StringBuilder().append(layoutId).append('_').append(1L).append('_').append(4L).toString();
					section_IdForSection4 = sectionPKVsSection_ID.get(pkKey);
					pkKey = new StringBuilder().append(layoutId).append('_').append(1L).append('_').append(3L).toString();
					section_IdForSection3 = sectionPKVsSection_ID.get(pkKey);
				}
				HashMap seqMap =new HashMap();
				
				//Ignite default event columns
				seqMap.put("SUBJECT",1);
				seqMap.put("LOCATION",2);
				seqMap.put("STARTDATETIME",3);
				seqMap.put("ENDDATETIME",4);
				seqMap.put("ALLDAYEVENT",5);
				seqMap.put("RECURRING",6);
				seqMap.put("REMINDAT",7);
				seqMap.put("SEID",8);
				seqMap.put("CONTACTID",8);
				seqMap.put("SMOWNERID",9);
				seqMap.put("DESCRIPTION",10);
				seqMap.put("PARTICIPANTID",11);
				seqMap.put("SMCREATORID",12);
				seqMap.put("MODIFIEDBY",30);
				seqMap.put("CREATEDTIME",32);
				seqMap.put("MODIFIEDTIME",34);
				seqMap.put("CHECKINCREATEDTIME",29);
				seqMap.put("TAGMODULEREFID",31);
				
				HashMap fieldMap=new HashMap();
				if(fielddObj!=null && !fielddObj.isEmpty()){
					ArrayList idArray=new ArrayList();
					Iterator fieldItr=fielddObj.getRows(CRMFIELD.TABLE);
					while(fieldItr.hasNext()){
						Row fieldRow=(Row) fieldItr.next();
						if(seqMap.containsKey(fieldRow.get(CRMFIELD.COLUMNNAME).toString())){
							idArray.add(fieldRow.get(CRMFIELD.FIELDID));
							fieldMap.put(Long.parseLong(fieldRow.get(CRMFIELD.FIELDID).toString()), fieldRow.get(CRMFIELD.COLUMNNAME).toString());
						}
					}
					Criteria cr=new Criteria(Column.getColumn(CRMMODEFIELD.TABLE, CRMMODEFIELD.FIELDID),idArray.toArray(),QueryConstants.NOT_IN);
					cr=cr.and(new Criteria(Column.getColumn(CRMMODEFIELD.TABLE, CRMMODEFIELD.LAYOUTID),layoutId,QueryConstants.EQUAL));
					per.delete(cr);
					//DataObject modfieldObj=CrmFieldUtil.getModeFieldsDOForModule(CrmModuleConstants.ACTIVITIES, zgid);
					SelectQuery sQry = new SelectQueryImpl(Table.getTable(CRMMODEFIELD.TABLE));
					sQry.addSelectColumn( Column.getColumn(null,"*") );
					Criteria modcr=new Criteria(Column.getColumn(CRMMODEFIELD.TABLE, CRMMODEFIELD.TABID), tabId , QueryConstants.EQUAL);
					sQry.setCriteria(modcr);
					DataObject modfieldObj = per.get(sQry);
					if(modfieldObj!=null && !modfieldObj.isEmpty()){
						
						//Section creation for more details and participants
						createSection(zgid, tabId,layoutId,3,"More Details",1);//No I18N
						createSection(zgid, tabId,layoutId,4,"Participant Details",1);//No I18N
						
						Iterator modItr=modfieldObj.getRows(CRMMODEFIELD.TABLE);
						while (modItr.hasNext()){
							Row modRow=(Row) modItr.next();	
							Long fieldId=Long.parseLong(modRow.get(CRMMODEFIELD.FIELDID).toString());
							if(fieldMap.containsKey(fieldId)){
								String columnName=(String) fieldMap.get(fieldId);
								modRow.set(CRMMODEFIELD.FIELDSEQUENCE, seqMap.get(columnName));
								if(columnName!=null && (columnName.indexOf("LOCATION")!=-1 || columnName.indexOf("SEID")!=-1 || columnName.indexOf("CONTACTID")!=-1 || columnName.indexOf("SMOWNERID")!=-1 || columnName.indexOf("REMINDAT")!=-1 || columnName.indexOf("DESCRIPTION")!=-1)){
									modRow.set(CRMMODEFIELD.SECTIONID, 3);
									modRow.set(CRMMODEFIELD.SECTION_ID, section_IdForSection3);
								}else if(columnName!=null && (columnName.indexOf("PARTICIPANTID")!=-1)){
									modRow.set(CRMMODEFIELD.SECTIONID, 4);
									modRow.set(CRMMODEFIELD.SECTION_ID, section_IdForSection4);
								}else{
									modRow.set(CRMMODEFIELD.SECTIONID, 1);
									modRow.set(CRMMODEFIELD.SECTION_ID, section_IdForSection1);
								}
								modfieldObj.updateRow(modRow);
							}
						}
						per.update(modfieldObj);
					}
				}
			}
		}catch(Exception e){
			
			LOGGER.log(Level.SEVERE, "Exception occurred while editing default events layout for Ignite user ::"+e);
			throw e;
		}
	}
	
	public void modifyCallLayout() throws Exception{
		
		try{
			new NewCallsViewMigrator().doCallsNewViewRelatedMigrationForBigin();
		}catch(Exception ex){
			LOGGER.log(Level.SEVERE, "Exception occurred while migrating to calls new view for Bigin user ::"+ex);
			throw ex;
		}
		/*try{
			Persistence per = PersistenceCacheUtil.getNonCachePersistenceHandle(zgid);
			DataObject fielddObj=CrmFieldUtil.getFieldsDOForModule(CrmModuleConstants.CALLS, zgid);
			if(fielddObj!=null && !fielddObj.isEmpty()){
				String tabId=""+fielddObj.getRow(CRMFIELD.TABLE).get(CRMFIELD.TABID);
				Long layoutId=PageLayoutUtil.getSystemdefinedActiveLayout(Long.parseLong(tabId),zgid);
				//createSection(zgid, tabId,layoutId,2,"Purpose of Outgoing Call");//No I18N
				//createSection(zgid, tabId,layoutId,3,"Outcome of Outgoing Call");//No I18N
				HashMap seqMap =new HashMap();
				
				//Ignite default calls layout
				seqMap.put("CONTACTID",1);
				seqMap.put("SEID",2);
				seqMap.put("CALLSTARTDATETIME",3);
				seqMap.put("CALLDURATION",4);
				seqMap.put("REMINDAT",5);
				seqMap.put("CALLTYPE",6);
				seqMap.put("SMOWNERID",7);
				seqMap.put("SUBJECT",8);
				seqMap.put("CALLPURPOSE",8);
				seqMap.put("CALLRESULT",9);
				seqMap.put("DESCRIPTION",10);
				seqMap.put("SMCREATORID",13);
				seqMap.put("MODIFIEDBY",14);
				seqMap.put("CREATEDTIME",16);
				seqMap.put("MODIFIEDTIME",17);
				seqMap.put("CLOSEDTIME",26);
				seqMap.put("CALLDURATIONINSEC",27);
				seqMap.put("CALLSTATUS", 28);
				HashMap fieldMap=new HashMap();
				if(fielddObj!=null && !fielddObj.isEmpty()){
					ArrayList idArray=new ArrayList();
					Iterator fieldItr=fielddObj.getRows(CRMFIELD.TABLE);
					while(fieldItr.hasNext()){
						Row fieldRow=(Row) fieldItr.next();
						if(seqMap.containsKey(fieldRow.get(CRMFIELD.COLUMNNAME).toString())){
							idArray.add(fieldRow.get(CRMFIELD.FIELDID));
							fieldMap.put(Long.parseLong(fieldRow.get(CRMFIELD.FIELDID).toString()), fieldRow.get(CRMFIELD.COLUMNNAME).toString());
						}
					}
					Criteria cr=new Criteria(Column.getColumn(CRMMODEFIELD.TABLE, CRMMODEFIELD.FIELDID),idArray.toArray(),QueryConstants.NOT_IN);
					cr=cr.and(new Criteria(Column.getColumn(CRMMODEFIELD.TABLE, CRMMODEFIELD.LAYOUTID),layoutId,QueryConstants.EQUAL));
					per.delete(cr);
					//DataObject modfieldObj=CrmFieldUtil.getModeFieldsDOForModule(CrmModuleConstants.ACTIVITIES, zgid);
					SelectQuery sQry = new SelectQueryImpl(Table.getTable(CRMMODEFIELD.TABLE));
					sQry.addSelectColumn( Column.getColumn(null,"*") );
					Criteria modcr=new Criteria(Column.getColumn(CRMMODEFIELD.TABLE, CRMMODEFIELD.TABID), tabId , QueryConstants.EQUAL);
					sQry.setCriteria(modcr);
					DataObject modfieldObj = per.get(sQry);
					if(modfieldObj!=null && !modfieldObj.isEmpty()){
						Iterator modItr=modfieldObj.getRows(CRMMODEFIELD.TABLE);
						while (modItr.hasNext()){
							Row modRow=(Row) modItr.next();	
							Long fieldId=Long.parseLong(modRow.get(CRMMODEFIELD.FIELDID).toString());
							if(fieldMap.containsKey(fieldId)){
								String columnName=(String) fieldMap.get(fieldId);
								modRow.set(CRMMODEFIELD.FIELDSEQUENCE, seqMap.get(columnName));
								if(columnName!=null && (columnName.indexOf("SUBJECT")!=-1 || columnName.indexOf("PURPOSE")!=-1)){
									modRow.set(CRMMODEFIELD.SECTIONID, 2);
								}else if(columnName!=null && (columnName.indexOf("RESULT")!=-1 || columnName.indexOf("DESCRIPTION")!=-1)){
									modRow.set(CRMMODEFIELD.SECTIONID, 3);
								}else{
									modRow.set(CRMMODEFIELD.SECTIONID, 1);
								}
								modfieldObj.updateRow(modRow);
							}
						}
						per.update(modfieldObj);
					}
				}
			}
		}catch(Exception e){
			
			LOGGER.log(Level.SEVERE, "Exception occurred while editing default calls layout for Ignite user ::"+e);
		}
		try{
			new NewCallsViewMigrator().handleCustomerDataUpdates();
		}catch(Exception ex){
			LOGGER.log(Level.SEVERE, "Exception occurred while executing calls new view migrator ::"+ex);
		}*/
	}
	
	public void modifyProductLayout(String zgid) throws Exception{
		try{
			Persistence per = PersistenceCacheUtil.getNonCachePersistenceHandle(zgid);
			DataObject fielddObj=FieldUtil.getFieldsDOForModule(CrmModuleConstants.PRODUCTS, zgid);
			if(fielddObj!=null && !fielddObj.isEmpty()){
				String tabId=""+fielddObj.getRow(CRMFIELD.TABLE).get(CRMFIELD.TABID);
				Long layoutId=PageLayoutUtil.getSystemdefinedActiveLayout(Long.parseLong(tabId),zgid);
				HashMap<String, Long> sectionPKVsSection_ID = SectionUtil.getSectionPKVsSection_IDMap(Long.valueOf(tabId), layoutId, null, zgid);
				Long section_IdForSection1 = null;
				Long section_IdForSection2 = null;
				if(sectionPKVsSection_ID != null){
					String pkKey = new StringBuilder().append(layoutId).append('_').append(CrmConstants.LAYOUT_DEFAULTMODE).append('_').append(1L).toString();
					section_IdForSection1 = sectionPKVsSection_ID.get(pkKey);
					pkKey = new StringBuilder().append(layoutId).append('_').append(CrmConstants.LAYOUT_DEFAULTMODE).append('_').append(5L).toString();
					section_IdForSection2 = sectionPKVsSection_ID.get(pkKey);
				}
				HashMap seqMap =new HashMap();
				
				//Ignite Default deal columns
				seqMap.put("PRODUCTNAME",1);
				seqMap.put("SMOWNERID",2);
				seqMap.put("PRODUCTCODE",3);
				seqMap.put("ACTIVE",4);
				//seqMap.put("MANUFACTURER",5);
				seqMap.put("CATEGORY",5);
				seqMap.put("UNITPRICE",6);
				seqMap.put("TAGMODULEREFID",7);
				seqMap.put("DESCRIPTION",8);
				seqMap.put("SMCREATORID",12);
				seqMap.put("MODIFIEDBY",13);
				seqMap.put("CREATEDTIME",14);
				seqMap.put("MODIFIEDTIME",15);
				seqMap.put("PHOTO_FILEID",1);
				//seqMap.put("SE_STATUS",16);
				
				HashMap fieldMap=new HashMap();
				if(fielddObj!=null && !fielddObj.isEmpty()){
					ArrayList idArray=new ArrayList();
					Iterator<Row> fieldItr=fielddObj.getRows(CRMFIELD.TABLE);
					while(fieldItr.hasNext()){
						Row fieldRow=fieldItr.next();
						if(seqMap.containsKey(fieldRow.get(CRMFIELD.COLUMNNAME).toString())){
							idArray.add(fieldRow.get(CRMFIELD.FIELDID));
							fieldMap.put(Long.parseLong(fieldRow.get(CRMFIELD.FIELDID).toString()), fieldRow.get(CRMFIELD.COLUMNNAME).toString());
						}
					}
					Criteria cr=new Criteria(Column.getColumn(CRMMODEFIELD.TABLE, CRMMODEFIELD.FIELDID),idArray.toArray(),QueryConstants.NOT_IN);
					cr=cr.and(new Criteria(Column.getColumn(CRMMODEFIELD.TABLE, CRMMODEFIELD.LAYOUTID),layoutId,QueryConstants.EQUAL));
					per.delete(cr);
					//DataObject modfieldObj=CrmFieldUtil.getModeFieldsDOForModule(CrmModuleConstants.ACTIVITIES, zgid);
					SelectQuery sQry = new SelectQueryImpl(Table.getTable(CRMMODEFIELD.TABLE));
					sQry.addSelectColumn( Column.getColumn(null,"*") );
					Criteria modcr=new Criteria(Column.getColumn(CRMMODEFIELD.TABLE, CRMMODEFIELD.TABID), tabId , QueryConstants.EQUAL);
					sQry.setCriteria(modcr);
					DataObject modfieldObj = per.get(sQry);
					if(modfieldObj!=null && !modfieldObj.isEmpty()){
						Iterator<Row> modItr=modfieldObj.getRows(CRMMODEFIELD.TABLE);
						while (modItr.hasNext()){
							Row modRow=modItr.next();	
							Long fieldId=Long.parseLong(modRow.get(CRMMODEFIELD.FIELDID).toString());
							if(fieldMap.containsKey(fieldId)){
								String columnName=(String) fieldMap.get(fieldId);
								modRow.set(CRMMODEFIELD.FIELDSEQUENCE, seqMap.get(columnName));
								if(columnName!=null && columnName.indexOf("PHOTO_FILEID")!=-1){
									modRow.set(CRMMODEFIELD.SECTIONID, 5);
									modRow.set(CRMMODEFIELD.SECTION_ID, section_IdForSection2);
								}else{
									modRow.set(CRMMODEFIELD.SECTIONID, 1);
									modRow.set(CRMMODEFIELD.SECTION_ID, section_IdForSection1);
								}
								modfieldObj.updateRow(modRow);
							}
						}
						per.update(modfieldObj);
					}
					
					//Section creation for additional information fields
					createSection(zgid, tabId,layoutId,2,"Additional Information",2);//No I18N
					
					//Update related list for deals
					HashMap listMap=new HashMap();
					listMap.put("POTENTIALSPERSONALITY", 2);
					listMap.put("ATTACHMENTSPERSONALITY", 3);
					
					RelatedListUtil.updateRelatedList(listMap,Long.parseLong(tabId));
				}
			}
		}catch(Exception e){
			
			LOGGER.log(Level.SEVERE, "Exception occurred while editing default product layout for Ignite user ::{0}",e);
			throw e;
		}
	}
	
	public void createSection(String zgid,String tabId,Long layoutId,int secseq,String sectionName,int mode) throws Exception{
		
		//Section creation for additional information fields
		try{
		Persistence per = PersistenceCacheUtil.getNonCachePersistenceHandle(zgid);
		DataObject secObj=per.constructDataObject();
		//The following part of the code determines the previous maximum sectionId and sectionSeq
		Long max_sectionId = null;
	    DataSet datas = null;
        SelectQueryImpl maxSecid = new SelectQueryImpl(Table.getTable(CRMSECTION.TABLE));
        maxSecid.addSelectColumn(Column.getColumn(CRMSECTION.TABLE, CRMSECTION.SECTIONID).maximum());
        maxSecid.addSelectColumn(Column.getColumn(CRMSECTION.TABLE, CRMSECTION.SECTIONSEQUENCE).maximum());
        Criteria seccr = new Criteria(Column.getColumn(CRMSECTION.TABLE, CRMSECTION.TABID), tabId, QueryConstants.EQUAL);
        seccr = seccr.and( new Criteria(Column.getColumn(CRMSECTION.TABLE, CRMSECTION.MODE), mode, QueryConstants.EQUAL) );
        maxSecid.setCriteria( seccr );
        
        RelationalAPI rapi = RelationalAPI.getInstance();
        try(Connection conn = rapi.getConnection())
	    {
		    datas = rapi.executeQuery(maxSecid, conn);
		    if (datas.next()) 
		    {
			    max_sectionId = (Long) datas.getValue(1);
		    } 
	    }
	    finally
	    {
		    if(datas!=null)
		    {
			    datas.close();
		    }
	    }
        
        //The following code will create new sections and give sequence and Ids from previous maximum
            max_sectionId++;
            Row section = new Row(CRMSECTION.TABLE);
            section.set(CRMSECTION.TABID, tabId);
            section.set(CRMSECTION.LAYOUTID, layoutId);
            section.set(CRMSECTION.SECTIONID, max_sectionId);
            section.set(CRMSECTION.SYSTEMNAME, sectionName); 
            section.set(CRMSECTION.NAME, sectionName);
            section.set(CRMSECTION.PRESENCE, 1L);
            section.set(CRMSECTION.SECTIONSEQUENCE, secseq);
            section.set(CRMSECTION.COLUMNCOUNT, CoreSectionConstants.COLUMNCOUNT.DOUBLE_CLOUMN_SECTION.getValue());
            section.set(CRMSECTION.GENERATEDTYPE, CoreSectionConstants.GENERATEDTYPE.SYSTEM_GENERATED_SECTION.getValue());
            section.set(CRMSECTION.TABTRAVERSAL, 2);
            section.set(CRMSECTION.SHOWTYPE, 7);
            section.set(CRMSECTION.MODE, mode);
            secObj.addRow(section);
            per.add(secObj);
        }catch(Exception e){
        	LOGGER.log(Level.SEVERE, "Exception while creating additional info section for ignite user ::"+e);
        	throw e;
        }
	}
	
	private void updateAccountsTab(String tabId,String zgid) throws Exception{
		
		try{
			
			UpdateQueryImpl updateTabName = new UpdateQueryImpl(CRMTAB.TABLE);
			Criteria cr = new Criteria(Column.getColumn(CRMTAB.TABLE,CRMTAB.TABID), tabId, QueryConstants.EQUAL);
			updateTabName.setCriteria(cr);
			updateTabName.setUpdateColumn(CRMTAB.TABLABEL, "Companies");//No I18N
			updateTabName.setUpdateColumn(CRMTAB.SINGULARLABEL, "Company");//No I18N
			PersistenceCacheUtil.getNonAIPersistenceHandle(zgid).update(updateTabName);	
		
		}catch(Exception e){
			LOGGER.log(Level.SEVERE, "Exception occurred while changing accounts tab name to companies ::"+e);
			throw e;
		}
	}
	
	public static String getIgniteHtmlMesg(String owner,String body,String fromName,Long userId) throws Exception	
	{
		Locale userLocale = null;
		String dearName ="";
		String regards="";
		if(userId!=null)
		{
			userLocale = UserUtil.getUserLangLocaleByUserId(userId);
		}
		//CrmResourceBundle resourceBundle = new CrmResourceBundle( userLocale );
		try
		{
			CrmResourceBundle resourceBundle=(CrmResourceBundle)ResourceUtil.getResourceBundleObj(userLocale);
	
			dearName = ResourceUtil.getResourceValueByKey("crm.general.hello",null,null,userLocale,new String[]{IAMEncoder.encodeHTML(owner)});//No I18N
			regards = (String)resourceBundle.handleGetObject("crm.task.customer.regards"); // No I18N
		}
		catch(Exception e)
		{
			LOGGER.log(Level.WARNING,e.toString(),e);
		}
	
		String htmlMesg=new StringBuilder("<html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" /><title>Zoho Bigin</title></head><body leftmargin=\"0\" topmargin=\"0\" marginheight=\"0\" marginwidth=\"0\" style=\"font-family:arial,helvetica,sans-serif;color:#666;\"> <div class=\"mT\" style=\"background-color: #F0F1F2;padding: 4%;\"><div style=\"width:100%;margin: 0 auto;box-shadow: 0 10px 10px 0 rgba(0,0,0,0.02);\"><div style=\"width: 100%;margin: 0px auto;background-color: #fff;border-bottom:  1px solid #eee;\"><div style=\"width: 100%;margin: 0 auto;padding: 15px 30px;\"><img src=\"cid:IgniteLogo.png\" alt=\"BiginLogo\" style=\"height: 30px;width: 75px;\"></div></div><div style=\"width: 100%;font-size: 16px; margin: 0 auto;background: #fff; font-family: lucida grande,lucida sans,lucida sans unicode,arial,helvetica,verdana,sans-serif;padding: 40px 30px;min-height: 250px;box-sizing: border-box;\">").toString();//No I18N
		htmlMesg+=new StringBuilder("<table width=\"100%\" border=\"0\" cellpadding=\"0\" style=\"margin: 0 auto;\" cellspacing=\"0\"><tbody><tr><td width=\"25%\" height=\"6\" align=\"left\" colspan=3 valign=\"top\"><font size=\"2\" face=\"Arial,Geneva,Helvetica,Swiss,SunSans-Regular\" color=\"#666\">").append(dearName).append(",").append("</font> <br></td></tr><tr><td width=\"25%\" height=\"6\" align=\"left\" colspan=3 valign=\"top\"><font size=\"2\" face=\"Arial,Geneva,Helvetica,Swiss,SunSans-Regular\" color=\"#666\">").append(body).append("</font> <br></td></tr></tbody></table></div>"); 
		htmlMesg+=new StringBuilder("<div style=\"background-color: #F4F5F6;height: auto;overflow: hidden;font-family: Roboto,lucida grande,lucida sans,lucida sans unicode,arial,helvetica,verdana,sans-serif;\"><div style=\"width: 100%;margin: 0 auto;padding: 15px 30px;\"><span style=\"display: block;color: #777;margin-bottom: 5px;\">").append(regards).append(",</span><span style=\"display: block;font-weight: bold;color: #555;\">").append(fromName).append("</span></div></div></div></div></body></html>");
		return htmlMesg;
	}
}

