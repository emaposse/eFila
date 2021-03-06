package model.manager.reports;


import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import model.manager.excel.conversion.exceptions.ReportException;

import org.celllife.idart.commonobjects.LocalObjects;
import org.celllife.idart.database.dao.ConexaoJDBC;
import org.celllife.idart.database.hibernate.StockCenter;
import org.celllife.idart.database.hibernate.User;
import org.eclipse.swt.widgets.Shell;

public class MiaReportMISAU extends AbstractJasperReport {
	
	private final StockCenter stockCenter;
	private final Date theEndDate;
	private Date theStartDate;
	


	public MiaReportMISAU(Shell parent, StockCenter stockCenter, Date theStartDate, Date theEndDate) {
		super(parent);
		this.stockCenter = stockCenter;
		this.theStartDate=theStartDate;
		this.theEndDate = theEndDate;
	}

	@Override
	protected void generateData() throws ReportException {
	}

	@Override
	protected Map<String, Object> getParameterMap() throws ReportException {
		
		ConexaoJDBC conn = new ConexaoJDBC();

		Map<String, Object> map = new HashMap<String, Object>();
				
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			
			//Total de pacientes que levantaram arv 20 a 20
			int totalpacientesfarmacia = conn.totalPacientesFarmacia(dateFormat.format(theStartDate), dateFormat.format(theEndDate));
		
			int totalpacientesinicio = conn.totalPacientesInicio(dateFormat.format(theStartDate), dateFormat.format(theEndDate));
			
			int totalpacientestransito = conn.totalPacientesEmTransito(dateFormat.format(theStartDate), dateFormat.format(theEndDate));
			
			int totalpacientesmanter = conn.totalPacientesManter(dateFormat.format(theStartDate), dateFormat.format(theEndDate));
			
            int totalpacientesalterar =conn.totalPacientesAlterar(dateFormat.format(theStartDate), dateFormat.format(theEndDate));
 				
 			int totalpacientesppe =conn.totalPacientesPPE(dateFormat.format(theStartDate), dateFormat.format(theEndDate));
  			
  			int totalpacienteptv =conn.totalPacientes_PTV(dateFormat.format(theStartDate), dateFormat.format(theEndDate));
			
			int mesesdispensados=conn.mesesDispensados(dateFormat.format(theStartDate), dateFormat.format(theEndDate));
			
			int pacientesEmTarv=conn.pacientesActivosEmTarv(dateFormat.format(theStartDate), dateFormat.format(theEndDate));
			
            int mesesdispensadosparaDT = conn.mesesDispensadosParaDT(dateFormat.format(theStartDate), dateFormat.format(theEndDate));
            
            int mesesdispensadosparaDS = conn.mesesDispensadosParaDS(dateFormat.format(theStartDate), dateFormat.format(theEndDate));
			
		map.put("stockCenterId", new Integer(stockCenter.getId()));
		map.put("date", theStartDate);
		map.put("dateFormat", dateFormat.format(theStartDate));
		map.put("monthStart", dateFormat.format(theStartDate));
		
		User localUser = LocalObjects.getUser(getHSession());
		
		map.put("username",localUser.getUsername());
		map.put("monthEnd", dateFormat.format(theEndDate));
		map.put("dateEnd", theEndDate);
		map.put("stockCenterName", stockCenter.getStockCenterName());
		map.put("path", getReportPath());

		map.put("facilityName", LocalObjects.currentClinic.getClinicName());
		map.put("pharmacist1", LocalObjects.pharmacy.getPharmacist());
		map.put("pharmacist2", LocalObjects.pharmacy.getAssistantPharmacist());
		map.put("totalpacientesfarmacia", String.valueOf(totalpacientesfarmacia)); 
		map.put("totalpacientesinicio",String.valueOf(totalpacientesinicio));
		map.put("totalpacientestransito",String.valueOf(totalpacientestransito));
		map.put("totalpacientesmanter",String.valueOf(totalpacientesmanter)); 
		map.put("totalpacientesalterar",String.valueOf(totalpacientesalterar));
		map.put("totalpacientesppe",String.valueOf(totalpacientesppe));
		map.put("totalpacienteptv",String.valueOf(totalpacienteptv));
		map.put("mesesdispensados",String.valueOf(mesesdispensados));
		map.put("pacientesEmTarv",String.valueOf(pacientesEmTarv));
		map.put("dataelaboracao", new SimpleDateFormat("dd/MM/yyyy").format(new Date())); 
		map.put("mes", mesPortugues(theStartDate));
		map.put("mes2",mesPortugues(theEndDate));
		map.put("totalpacientesdt",String.valueOf(mesesdispensadosparaDT));
		map.put("totalpacientesds",String.valueOf(mesesdispensadosparaDS));

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return map;
	}


	@Override
	protected String getReportFileName() {
		return "MmiaReportMISAU";
	}
	


	private String mesPortugues(Date data ) {
		
		String mes="";
		
		Calendar calendar = new GregorianCalendar();
		 Date trialTime = data;
		 calendar.setTime(trialTime);
		 

		int mesint =calendar.get(Calendar.MONTH);
		
		System.out.println(mesint);
		
		switch(mesint) {
			case 0: mes="Janeiro";break;
			case 1: mes="Fevereiro";break;
			case 2: mes="Março";break;
			case 3: mes="Abril";break;
			case 4: mes="Maio";break;
			case 5: mes="Junho";break;
			case 6: mes="Julho";break;
			case 7: mes="Agosto";break;
			case 8: mes="Setembro";break;
			case 9: mes="Outubro";break;
			case 10: mes="Novembro";break;
			case 11: mes="Dezembro";break;
			default:mes="";break;
		}
		
		return mes;
	}
}