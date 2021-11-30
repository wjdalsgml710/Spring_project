package com.example.sproject.model.calendar;

import lombok.Data;

@Data
public class Calendar {
	private int cl_num;
	private String m_id;
	private String cl_name;
	private String cl_content;
	private String cl_sdate;
	private String cl_edate;
	private int cg_num;
	private int cl_term;
	

	private String cl_group;
	private String cl_color;
	private String cl_groupname;
}
