/*
 * iDART: The Intelligent Dispensing of Antiretroviral Treatment
 * Copyright (C) 2006 Cell-Life
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 as published by
 * the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License version
 * 2 for more details.
 *
 * You should have received a copy of the GNU General Public License version 2
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 */
package org.celllife.idart.gui.generalAdmin;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;

import model.manager.excel.reports.in.PatientSheet;

import org.apache.log4j.Logger;
import org.celllife.idart.commonobjects.LocalObjects;
import org.celllife.idart.commonobjects.iDartProperties;
import org.celllife.idart.database.dao.ConexaoJDBC;
import org.celllife.idart.database.hibernate.util.HibernateUtil;
import org.celllife.idart.gui.clinic.AddClinic;
import org.celllife.idart.gui.doctor.AddDoctor;
import org.celllife.idart.gui.drug.AddDrug;
import org.celllife.idart.gui.drugGroup.AddDrugGroup;
import org.celllife.idart.gui.platform.GenericAdminGui;
import org.celllife.idart.gui.platform.GenericFormGui;
import org.celllife.idart.gui.regimeTerapeutico.AddRegimeTerapeutico;
import org.celllife.idart.gui.stockCenter.StockCenterInfo;
import org.celllife.idart.gui.sync.patients.Sync;
import org.celllife.idart.gui.user.ManagePharmUsers;
import org.celllife.idart.gui.utils.ResourceUtils;
import org.celllife.idart.gui.utils.iDartFont;
import org.celllife.idart.gui.utils.iDartImage;
import org.celllife.idart.messages.Messages;
import org.celllife.idart.misc.MessageUtil;
import org.celllife.idart.misc.SafeSaveDialog;
import org.celllife.idart.misc.SafeSaveDialog.FileType;
import org.celllife.idart.misc.task.Import;
import org.celllife.idart.misc.task.TaskException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.hibernate.Session;

/**
 */
public class GeneralAdmin extends GenericAdminGui {

    /**
     * Constructor for GeneralAdmin.
     *
     * @param parent Shell
     */
    public GeneralAdmin(Shell parent) {
        super(parent);
    }

    @Override
    protected void createShell() {
        getLog().info(Messages.getString("GeneralAdmin.title.header")); //$NON-NLS-1$
        String shellText = Messages.getString("GeneralAdmin.title.update"); //$NON-NLS-1$
        buildShell(shellText);
    }

    /**
     * This method initializes compHeader
     */
    @Override
    protected void createCompHeader() {
        String headerText = Messages.getString("GeneralAdmin.title.update"); //$NON-NLS-1$
        iDartImage icoImage = iDartImage.GENERALADMIN;
        // Building the component from the GenericAdminGui
        buildCompHeader(headerText, icoImage);
    }

    @Override
    protected void createCompOptions() {
        createMyGroups();
    }

    private void createMyGroups() {
        // create the 4 groups
        createGrpPharmacy();
        createGrpImport();
        createGrpClinic();
        createGrpDrug();
        createGrpDoctor();
        createGrpDrugGroup();
    }

    @Override
    protected void setLogger() {
        setLog(Logger.getLogger(this.getClass()));
    }

    /**
     * This method initializes grpClinics
     */
    private void createGrpClinic() {

        // grpClinics
        Group grpClinics = new Group(getCompOptions(), SWT.NONE);
        grpClinics.setBounds(new Rectangle(495, 13, 305, 150));
        grpClinics.setText(Messages.getString("GeneralAdmin.group.title")); //$NON-NLS-1$
        grpClinics.setFont(ResourceUtils.getFont(iDartFont.VERASANS_12));

        // lblPicClinics
        Label lblPicClinics = new Label(grpClinics, SWT.NONE);
        lblPicClinics.setBounds(new org.eclipse.swt.graphics.Rectangle(6, 0,
                50, 43));
        lblPicClinics.setText(EMPTY);
        lblPicClinics.setImage(ResourceUtils.getImage(iDartImage.CLINIC));

        // btnClinicsAdd
        Button btnClinicsAdd = new Button(grpClinics, SWT.NONE);
        btnClinicsAdd.setBounds(new org.eclipse.swt.graphics.Rectangle(35, 55,
                235, 30));
        btnClinicsAdd
                .setToolTipText(Messages.getString("GeneralAdmin.button.tooltip")); //$NON-NLS-1$
        btnClinicsAdd.setText(Messages.getString("GeneralAdmin.button.title")); //$NON-NLS-1$
        btnClinicsAdd.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
        btnClinicsAdd
                .addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
                    @Override
                    public void widgetSelected(
                            org.eclipse.swt.events.SelectionEvent e) {
                                cmd_clinicsAdd();
                            }
                });
        btnClinicsAdd.setEnabled(false);
        // btnClinicsUpdate
        Button btnClinicsUpdate = new Button(grpClinics, SWT.NONE);
        btnClinicsUpdate.setBounds(new org.eclipse.swt.graphics.Rectangle(35,
                100, 235, 30));
        btnClinicsUpdate
                .setToolTipText(Messages.getString("GeneralAdmin.clinic.button.tooltip")); //$NON-NLS-1$

        btnClinicsUpdate.setText(Messages.getString("GeneralAdmin.clinic.button.title")); //$NON-NLS-1$
        btnClinicsUpdate.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
        btnClinicsUpdate
                .addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
                    @Override
                    public void widgetSelected(
                            org.eclipse.swt.events.SelectionEvent e) {
                                cmd_clinicsUpdate();
                            }
                });
        btnClinicsUpdate.setEnabled(false);
    }

    /**
     * This method initializes grpDoctors
     */
    private void createGrpDoctor() {

        // grpDoctors
        Group grpDoctors = new Group(getCompOptions(), SWT.NONE);
        grpDoctors.setBounds(new Rectangle(495, 329, 305, 150));
        grpDoctors.setText(Messages.getString("GeneralAdmin.doctors.group.title")); //$NON-NLS-1$
        grpDoctors.setFont(ResourceUtils.getFont(iDartFont.VERASANS_12));

        // lblPicDoctors
        Label lblPicDoctors = new Label(grpDoctors, SWT.NONE);
        lblPicDoctors.setBounds(new org.eclipse.swt.graphics.Rectangle(6, 0,
                50, 43));
        lblPicDoctors.setText(EMPTY);
        lblPicDoctors.setImage(ResourceUtils.getImage(iDartImage.DOCTOR));

        // btnDoctorsAdd
        Button btnDoctorsAdd = new Button(grpDoctors, SWT.NONE);
        btnDoctorsAdd.setText(Messages.getString("GeneralAdmin.doctors.button.title")); //$NON-NLS-1$
        btnDoctorsAdd
                .setToolTipText(Messages.getString("GeneralAdmin.doctors.button.tooltip")); //$NON-NLS-1$
        btnDoctorsAdd.setBounds(new org.eclipse.swt.graphics.Rectangle(35, 55,
                235, 30));
        btnDoctorsAdd.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
        btnDoctorsAdd
                .addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
                    @Override
                    public void widgetSelected(
                            org.eclipse.swt.events.SelectionEvent e) {
                                cmd_doctorAdd();
                            }
                });

        //Desactivar add clinico para user normal
        if (getUserPermission() != 'A') {
            btnDoctorsAdd.setEnabled(false);
        }

        // btnDoctorsUpdate
        Button btnDoctorsUpdate = new Button(grpDoctors, SWT.NONE);
        btnDoctorsUpdate.setText(Messages.getString("GeneralAdmin.button.docupdate.title")); //$NON-NLS-1$
        btnDoctorsUpdate
                .setToolTipText(Messages.getString("GeneralAdmin.button.docupdate.tooltip")); //$NON-NLS-1$
        btnDoctorsUpdate.setBounds(new org.eclipse.swt.graphics.Rectangle(35,
                100, 235, 30));
        btnDoctorsUpdate.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
        btnDoctorsUpdate
                .addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
                    @Override
                    public void widgetSelected(
                            org.eclipse.swt.events.SelectionEvent e) {

                                cmd_doctorUpdate();

                            }
                });
        //Desactivar UPDATE clinico para user normal
        if (getUserPermission() != 'A') {
            btnDoctorsUpdate.setEnabled(false);
        }
    }

    /**
     * This method initializes grpDrugs
     */
    private void createGrpDrug() {

        // grpDrugs
        Group grpDrugs = new Group(getCompOptions(), SWT.NONE);
        grpDrugs.setBounds(new Rectangle(50, 329, 305, 150));
        grpDrugs.setText(Messages.getString("GeneralAdmin.group.drug.title")); //$NON-NLS-1$
        grpDrugs.setFont(ResourceUtils.getFont(iDartFont.VERASANS_12));

        // lblPicDrugs
        Label lblPicDrugs = new Label(grpDrugs, SWT.NONE);
        lblPicDrugs.setBounds(new org.eclipse.swt.graphics.Rectangle(6, 0, 50,
                43));
        lblPicDrugs.setText(EMPTY);
        lblPicDrugs.setImage(ResourceUtils
                .getImage(iDartImage.PRESCRIPTIONADDDRUG));

        // btnDrugsAdd
        Button btnDrugsAdd = new Button(grpDrugs, SWT.NONE);
        btnDrugsAdd.setText(Messages.getString("GeneralAdmin.button.drug.title")); //$NON-NLS-1$
        btnDrugsAdd
                .setToolTipText(Messages.getString("GeneralAdmin.button.drug.tooltip")); //$NON-NLS-1$
        btnDrugsAdd.setBounds(new org.eclipse.swt.graphics.Rectangle(35, 55,
                235, 30));
        btnDrugsAdd.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
        btnDrugsAdd
                .addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
                    @Override
                    public void widgetSelected(
                            org.eclipse.swt.events.SelectionEvent e) {
                                cmd_drugsAdd();
                            }
                });

        //Desactivar a adicao de DRUG
        btnDrugsAdd.setEnabled(true);
        
             //Desactivar add Medicamento para user normal
        if (getUserPermission() != 'A' && getUserPermission() != 'C') {
            btnDrugsAdd.setEnabled(false);
        }
        
        // btnDrugsUpdate
        Button btnDrugsUpdate = new Button(grpDrugs, SWT.NONE);
        btnDrugsUpdate.setText(Messages.getString("GeneralAdmin.button.drugupdate.title")); //$NON-NLS-1$
        btnDrugsUpdate
                .setToolTipText(Messages.getString("GeneralAdmin.button.drugupdate.tooltip")); //$NON-NLS-1$
        btnDrugsUpdate.setBounds(new org.eclipse.swt.graphics.Rectangle(35,
                100, 235, 30));
        btnDrugsUpdate.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
        btnDrugsUpdate
                .addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
                    @Override
                    public void widgetSelected(
                            org.eclipse.swt.events.SelectionEvent e) {
                                cmd_drugsUpdate();
                            }
                });

        //Desactivar a actualizar DRUG
        btnDrugsUpdate.setEnabled(true);
        
        //Desactivar add Medicamento para user normal
        if (getUserPermission() != 'A' && getUserPermission() != 'C') {
            btnDrugsUpdate.setEnabled(false);
        }

//        if (!iDartProperties.enableDrugEditor) {
//            btnDrugsAdd.setEnabled(false);
//            btnDrugsUpdate.setEnabled(false);
//        }
    }

    /**
     * This method initializes grpDoctors
     */
    private void createGrpDrugGroup() {

        // grpDrugGroups
        Group grpDrugGroups = new Group(getCompOptions(), SWT.NONE);
        grpDrugGroups.setBounds(new Rectangle(495, 171, 305, 150));
        grpDrugGroups.setText(Messages.getString("GeneralAdmin.group.drugupdate.title")); //$NON-NLS-1$
        grpDrugGroups.setFont(ResourceUtils.getFont(iDartFont.VERASANS_12));

        // lblPicDoctors
        Label lblPicDrugGroups = new Label(grpDrugGroups, SWT.NONE);
        lblPicDrugGroups.setBounds(new org.eclipse.swt.graphics.Rectangle(6, 0,
                50, 43));
        lblPicDrugGroups.setText(EMPTY);
        lblPicDrugGroups.setImage(ResourceUtils.getImage(iDartImage.DRUGGROUP));

        // btnRegimenAdd
        Button btnRegimenAdd = new Button(grpDrugGroups, SWT.NONE);
        btnRegimenAdd.setText(Messages.getString("GeneralAdmin.button.regimen.title")); //$NON-NLS-1$
        btnRegimenAdd
                .setToolTipText(Messages.getString("GeneralAdmin.button.regimen.tooltip")); //$NON-NLS-1$
        btnRegimenAdd.setBounds(new org.eclipse.swt.graphics.Rectangle(35, 55,
                235, 30));
        btnRegimenAdd.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
        btnRegimenAdd
                .addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
                    @Override
                    public void widgetSelected(
                            org.eclipse.swt.events.SelectionEvent e) {
                                cmd_regimenAdd();
                            }
                });
        btnRegimenAdd.setEnabled(true);
          //Desactivar add Regime Terapeutico para user normal
        if (getUserPermission() != 'A' && getUserPermission() != 'C') {
            btnRegimenAdd.setEnabled(false);
        }

        // btnRegimenUpdate
        Button btnRegimenUpdate = new Button(grpDrugGroups, SWT.NONE);
        btnRegimenUpdate.setText(Messages.getString("GeneralAdmin.button.regimenupdate.title")); //$NON-NLS-1$
        btnRegimenUpdate
                .setToolTipText(Messages.getString("GeneralAdmin.button.regimenupdate.tooltip")); //$NON-NLS-1$
        btnRegimenUpdate.setBounds(new org.eclipse.swt.graphics.Rectangle(35,
                100, 235, 30));
        btnRegimenUpdate.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
        btnRegimenUpdate
                .addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
                    @Override
                    public void widgetSelected(
                            org.eclipse.swt.events.SelectionEvent e) {
                                cmd_regimenUpdate();
                            }
                });

        btnRegimenUpdate.setEnabled(true);

           //Desactivar add Regime Terapeutico para user normal
        if (getUserPermission() != 'A' && getUserPermission() != 'C') {
            btnRegimenAdd.setEnabled(false);
        }
        
//        if (!iDartProperties.enableDrugGroupEditor) {
//            btnRegimenAdd.setEnabled(false);
//            btnRegimenUpdate.setEnabled(false);
//        }
    }

    /**
     * This method initializes grpDoctors
     */
    private void createGrpImport() {

        Group grpImport = new Group(getCompOptions(), SWT.NONE);
        grpImport.setBounds(new Rectangle(50, 171, 305, 150));
        grpImport.setText(Messages.getString("GeneralAdmin.group.import.title")); //$NON-NLS-1$
        grpImport.setFont(ResourceUtils.getFont(iDartFont.VERASANS_12));

        Label lblPicImport = new Label(grpImport, SWT.NONE);
        lblPicImport.setBounds(new org.eclipse.swt.graphics.Rectangle(6, 0,
                50, 43));
        lblPicImport.setText(EMPTY);
        lblPicImport.setImage(ResourceUtils.getImage(iDartImage.PATIENTADMIN));

        Button btnGenerateTemplate = new Button(grpImport, SWT.NONE);
        btnGenerateTemplate.setText(Messages.getString("GeneralAdmin.button.generateTemplate.title")); //$NON-NLS-1$
        btnGenerateTemplate
                .setToolTipText(Messages.getString("GeneralAdmin.button.generateTemplate.tooltip")); //$NON-NLS-1$
        btnGenerateTemplate.setBounds(new org.eclipse.swt.graphics.Rectangle(35, 35,
                235, 30));
        btnGenerateTemplate.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
        btnGenerateTemplate
                .addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
                    @Override
                    public void widgetSelected(
                            org.eclipse.swt.events.SelectionEvent e) {
                                cmd_generateTemplate();
                            }
                });

        btnGenerateTemplate.setEnabled(false);

        //  
        Button btnImportPatients = new Button(grpImport, SWT.NONE);
        btnImportPatients.setText(Messages.getString("GeneralAdmin.button.importPatients.title")); //$NON-NLS-1$
        btnImportPatients
                .setToolTipText(Messages.getString("GeneralAdmin.button.importPatients.tooltip")); //$NON-NLS-1$
        btnImportPatients.setBounds(new org.eclipse.swt.graphics.Rectangle(35,
                70, 235, 30));
        btnImportPatients.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
        btnImportPatients
                .addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
                    @Override
                    public void widgetSelected(
                            org.eclipse.swt.events.SelectionEvent e) {
                                cmd_importPatients();
                            }
                });

        btnImportPatients.setEnabled(false);

        // Actuliazado - harmonizacao -- btnImportPatientsSESP
//        Button btnImportPatientsSESP = new Button(grpImport, SWT.NONE);
//        btnImportPatientsSESP.setText(Messages.getString("GeneralAdmin.button.importPatients.title2")); //$NON-NLS-1$
//        btnImportPatientsSESP
//                .setToolTipText(Messages.getString("GeneralAdmin.button.importPatients.tooltip2")); //$NON-NLS-1$
//        btnImportPatientsSESP.setBounds(new org.eclipse.swt.graphics.Rectangle(35,
//                110, 235, 30));
//        btnImportPatientsSESP.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
//        btnImportPatientsSESP
//                .addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
//                    @Override
//                    public void widgetSelected(
//                            org.eclipse.swt.events.SelectionEvent e) {
//
//                                cmd_importPatientsSESP();
//                            }
//                });
//        btnImportPatientsSESP.setEnabled(false);
//
         // Importar Pacientes do OpenMRS  : Alterado Colaco 06-07-2016
            Button btnImportPatientsOpenMRS = new Button(grpImport, SWT.NONE);
            btnImportPatientsOpenMRS.setText(Messages.getString("GeneralAdmin.button.openmrs.importPatients.title")); //$NON-NLS-1$
            btnImportPatientsOpenMRS
                    .setToolTipText(Messages.getString("GeneralAdmin.button.openmrs.importPatients.tooltip")); //$NON-NLS-1$
            btnImportPatientsOpenMRS.setBounds(new org.eclipse.swt.graphics.Rectangle(35,
                    110, 235, 30));
            btnImportPatientsOpenMRS.setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
            btnImportPatientsOpenMRS
                    .addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
                        @Override
                        public void widgetSelected(
                                org.eclipse.swt.events.SelectionEvent e) {
//                            MainPanel importPatients = new MainPanel();
//                            importPatients.createAndShowGUI();
                        }
                    });
            btnImportPatientsOpenMRS.setEnabled(true);
        
    }

    /**
     * This method initializes grpPharmacy
     */
    private void createGrpPharmacy() {

        // grpPharmacy
        Group grpPharmacy = new Group(getCompOptions(), SWT.NONE);
        grpPharmacy.setBounds(new Rectangle(50, 13, 305, 150));
        grpPharmacy.setText(Messages.getString("GeneralAdmin.group.pharmacy.title")); //$NON-NLS-1$
        grpPharmacy.setFont(ResourceUtils.getFont(iDartFont.VERASANS_12));

        // lblPicPharmacy
        Label lblPicPharmacy = new Label(grpPharmacy, SWT.NONE);
        lblPicPharmacy.setBounds(new org.eclipse.swt.graphics.Rectangle(10, 0,
                50, 43));
        lblPicPharmacy.setText(EMPTY);
        lblPicPharmacy
                .setImage(ResourceUtils.getImage(iDartImage.PHARMACYUSER));

        // btnManagePharmUsers
        Button btnManagePharmUsers = new Button(grpPharmacy, SWT.NONE);
        btnManagePharmUsers.setBounds(new org.eclipse.swt.graphics.Rectangle(
                35, 100, 235, 30));
        btnManagePharmUsers
                .setToolTipText(Messages.getString("GeneralAdmin.button.pharmacy.tooltip")); //$NON-NLS-1$
        btnManagePharmUsers.setText(Messages.getString("GeneralAdmin.button.pharmacy.title")); //$NON-NLS-1$
        btnManagePharmUsers
                .setFont(ResourceUtils.getFont(iDartFont.VERASANS_8));
        btnManagePharmUsers
                .addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
                    @Override
                    public void widgetSelected(
                            org.eclipse.swt.events.SelectionEvent e) {
                                cmd_managePharmUsers();
                            }
                });

        // btnPharmDetailsUpdate
        Button btnPharmDetailsUpdate = new Button(grpPharmacy, SWT.NONE);
        btnPharmDetailsUpdate.setBounds(new org.eclipse.swt.graphics.Rectangle(
                35, 55, 235, 30));
        btnPharmDetailsUpdate
                .setToolTipText(Messages.getString("GeneralAdmin.button.pharmdetails.tooltip")); //$NON-NLS-1$
        btnPharmDetailsUpdate.setText(Messages.getString("GeneralAdmin.button.pharmdetails.title")); //$NON-NLS-1$
        btnPharmDetailsUpdate.setFont(ResourceUtils
                .getFont(iDartFont.VERASANS_8));
        btnPharmDetailsUpdate
                .addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
                    @Override
                    public void widgetSelected(
                            org.eclipse.swt.events.SelectionEvent e) {
                                cmd_pharmUsersUpdate();
                            }
                });
        btnPharmDetailsUpdate.setEnabled(false);
    }

    public void cmd_clinicsAdd() {
        // AddClinic(true) to ADD new clinic
        AddClinic.addInitialisationOption(GenericFormGui.OPTION_isAddNotUpdate,
                true);
        new AddClinic(getShell());
    }

    public void cmd_clinicsUpdate() {
        // AddClinic(false) to UPDATE existing clinic
        AddClinic.addInitialisationOption(GenericFormGui.OPTION_isAddNotUpdate,
                false);
        new AddClinic(getShell());
    }

    public void cmd_doctorAdd() {
        // AddDoctor(true) to ADD new doctor
        AddDoctor.addInitialisationOption(GenericFormGui.OPTION_isAddNotUpdate,
                true);
        new AddDoctor(getShell());
    }

    public void cmd_doctorUpdate() {
        // AddDoctor(false) to UPDATE existing doctor
        AddDoctor.addInitialisationOption(GenericFormGui.OPTION_isAddNotUpdate,
                false);
        new AddDoctor(getShell());
    }

    public void cmd_drugsAdd() {
        // AddDrug(true) to ADD new drug
        AddDrug.addInitialisationOption(GenericFormGui.OPTION_isAddNotUpdate,
                true);
        new AddDrug(getShell());
    }

    public void cmd_drugsUpdate() {
        // AddDrug(false) to UPDATE existing drug
        AddDrug.addInitialisationOption(GenericFormGui.OPTION_isAddNotUpdate,
                false);
        new AddDrug(getShell());
    }

    public void cmd_managePharmUsers() {
        ManagePharmUsers.addInitialisationOption(
                GenericFormGui.OPTION_isAddNotUpdate, true);
        new ManagePharmUsers(getShell());
    }

    public void cmd_pharmUsersUpdate() {
        new StockCenterInfo(getShell());
    }
    /*Adicionado por colaco 14.01.2020: Comentar a adicao do Regimen*/
//	public void cmd_regimenAdd() {
//		// AddDrgGroup(true) to ADD new regimen
//		AddDrugGroup.addInitialisationOption(
//				GenericFormGui.OPTION_isAddNotUpdate, true);
//		new AddDrugGroup(getShell());
//	}

    /*Adicionado por colaco 14.01.2020: Adicionar regime terapeutico*/
    public void cmd_regimenAdd() {
        // AddRegimeTerapeutico(true) Adiciona novo regime Terapeutico
        AddRegimeTerapeutico.addInitialisationOption(
                GenericFormGui.OPTION_isAddNotUpdate, true);
        new AddRegimeTerapeutico(getShell());
    }
 /*Adicionado por colaco 14.01.2020: Actualiza regime terapeutico*/
    public void cmd_regimenUpdate() {
        AddRegimeTerapeutico.addInitialisationOption(
                GenericFormGui.OPTION_isAddNotUpdate, false);
        // AddRegimeTerapeutico(false) actualiza um regime terepeutico existente
        new AddRegimeTerapeutico(getShell());
    }

    /*Adicionado por colaco 14.01.2020: Comentar a actualizacao do Regimen*/
//     public void cmd_regimenUpdate() {
//        AddDrugGroup.addInitialisationOption(
//                GenericFormGui.OPTION_isAddNotUpdate, false);
//        // UpdateRegimen(false) to UPDATE existing regimen
//        new AddDrugGroup(getShell());
//    }
//     
    protected void cmd_importPatients() {
        FileDialog dlg = new FileDialog(getShell(), SWT.SAVE);
        dlg.setFilterExtensions(FileType.EXCEL.getFilterExtensions());
        dlg.setFilterNames(FileType.EXCEL.getFilterNames());
        String fileName = dlg.open();
        if (fileName == null) {
            return;
        }

        String sheet = "Sheet1";
        InputDialog sheetInput = new InputDialog(getShell(),
                "Nome do sheeet Excel",
                "Por favor inserir o nome do sheet Excel para importação",
                sheet, null);
        if (sheetInput.open() == Window.OK) {
            sheet = sheetInput.getValue();
        } else {
            return;
        }

        final Import task = new Import();
        task.setSheet(sheet);
        task.setFileName(fileName);
        try {
            new ProgressMonitorDialog(null).run(true, true, new IRunnableWithProgress() {
                @Override
                public void run(IProgressMonitor monitor) throws InvocationTargetException,
                        InterruptedException {
                    try {
                        task.run(monitor);
                    } catch (TaskException e) {
                        throw new InvocationTargetException(e);
                    }
                }
            });
            int errorCount = task.getErrorCount();
            if (errorCount > 0) {
                boolean open = MessageDialog.openQuestion(null, "Completado com erros",
                        "A importação foi completado com sucesso mas houve "
                        + +errorCount + " erros. Quer abrir o ficheiro log de erros?");
                if (open) {
                    Program.launch(task.getErrorFile().getAbsolutePath());
                }
            } else {
                MessageDialog
                        .openInformation(null, "Completado", "Importação completeda com successo sem"
                                + " erros");
            }
        } catch (InvocationTargetException e) {
            MessageUtil.showError(e, "Erro ao correr a tarefa",
                    e.getMessage());
        } catch (InterruptedException e) {
            MessageDialog
                    .openInformation(null, "Cancelado", e.getMessage());
        }

    }

    protected void cmd_generateTemplate() {
        SafeSaveDialog dialog = new SafeSaveDialog(getShell(), FileType.EXCEL);
        String fileName = dialog.open();
        if (fileName == null) {
            return;
        }
        PatientSheet sheet = new PatientSheet("Sheet1");
        Session session = HibernateUtil.getNewSession();
        sheet.setSession(session);
        sheet.init();
        sheet.writeTemplateSheet(fileName);
        session.close();
        Program.launch(fileName);
    }

    @Override
    protected void cmdCloseSelectedWidget() {
        cmdCloseSelected();
    }

    @Override
    public char getUserPermission() {

        return LocalObjects.getUser(getHSession()).getPermission();
    }

    //sync sesp patients
    protected void cmd_importPatientsSESP() {

        //ConexaoODBC odbc=new ConexaoODBC();
        Connection c = null;

        ConexaoJDBC jdbc = new ConexaoJDBC();
        jdbc.delete_sync_temp_patients();
        jdbc.insere_sync_temp_patients();
        try {

		 //c=odbc.getConnection();
        } catch (Exception e) {

            e.printStackTrace();
        }
        ConexaoJDBC conn = new ConexaoJDBC();
        if (c == null) {
            //se não houver conexao
            MessageBox conexaoACCESS = new MessageBox(new Shell(), SWT.ICON_ERROR
                    | SWT.OK);
            conexaoACCESS.setText("Conexão com Base de Dados SESP");
            conexaoACCESS
                    .setMessage("O iDART não está a se conectar com o SESP.\n Por favor verifique se os cabos da rede estão ligados no seu \ncomputador ou se o computador com SESP esta ligado!\n Saia do iDART e verifique estes apectos depois volte a entrar,\n se o problema persistir, não será possivel importar os pacientes do SESP");
            conexaoACCESS.open();

        } else if (conn.sync_table_patients().size() < 1) {
            //se os dados estiverem sincronizados
            MessageBox conexaoACCESS = new MessageBox(new Shell(), SWT.ICON_INFORMATION
                    | SWT.OK);
            conexaoACCESS.setText("Importação de Pacientes");
            conexaoACCESS
                    .setMessage("TODOS PACIENTES DO SESP ESTÃO NO iDART");
            conexaoACCESS.open();
            conn.delete_sync_temp_patients();

        } else {
			//Sincroniza

            new Sync(getShell(), false);
        }

    }
}
