-- for patient
CREATE INDEX patient_ID_index
ON Patient (patient_ID);
CREATE INDEX patient_name_index
ON Patient (name);
CREATE INDEX patient_gtype_index
ON Patient (gtype);
CREATE INDEX patient_age_index
ON Patient (age);
CREATE INDEX patient_address_index
ON Patient (address);
CREATE INDEX patient_number_of_appts_index
ON Patient (number_of_appts);

-- for hospital
CREATE INDEX hospital_ID_index
ON Hospital (hospital_ID);
CREATE INDEX hospital_name_idex
ON Hospital (name);

-- for department
CREATE INDEX depart_ID_index
ON Department (dept_ID);
CREATE INDEX depart_name_index
ON Department (name);
CREATE INDEX depart_hid_index
ON Department (hid);

-- for staff
CREATE INDEX staff_ID_index
ON Staff (staff_ID);
CREATE INDEX staff_name_index
ON Staff (name);
CREATE INDEX staff_hid_index
ON Staff (hid);

--  for Doctor
CREATE INDEX doctor_ID_index
ON Doctor (doctor_ID);
CREATE INDEX doctor_name_index
ON Doctor (name);
CREATE INDEX doctor_specialty_index
ON Doctor (specialty);
CREATE INDEX doctor_did_index
ON Doctor (did);

-- for Appointment
CREATE INDEX appnt_ID_index
ON Appointment (appnt_ID);
CREATE INDEX appnt_adate_index
ON Appointment (adate);
CREATE INDEX appnt_time_slot_index
ON Appointment (time_slot);
CREATE INDEX appnt_status_index
ON Appointment (status);

-- for request_maintenance
CREATE INDEX rm_patient_per_hour_index
ON request_maintenance (patient_per_hour);
CREATE INDEX rm_dept_name_index
ON request_maintenance (dept_name);
CREATE INDEX rm_time_slot_index
ON request_maintenance (time_slot);
CREATE INDEX rm_did_index
ON request_maintenance (did);
CREATE INDEX rm_sid_index
ON request_maintenance (sid);

-- for searches
CREATE INDEX searches_hid_index
ON searches (hid);
CREATE INDEX searches_pid_index
ON searches (pid);
CREATE INDEX searches_aid_index
ON searches (aid);

-- for schedules
CREATE INDEX schedules_appt_id_index
ON schedules (appt_id);
CREATE INDEX schedules_staff_id_index
ON schedules (staff_id);

-- has_appointment
CREATE INDEX ha_appt_id_index
ON has_appointment (appt_id);
CREATE INDEX ha_doctor_id_index
ON has_appointment (doctor_id);