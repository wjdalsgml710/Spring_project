package com.example.sproject.service.sign;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.sproject.dao.sign.SignDao;
import com.example.sproject.model.globals.GlobalsOfSg_status;
import com.example.sproject.model.login.Member;
import com.example.sproject.model.sign.Sign;
import com.example.sproject.model.sign.SignContent;
import com.example.sproject.model.sign.SignForm;
import com.example.sproject.model.sign.SignLine;

@Service
public class SignServiceImpl implements SignService {
	@Autowired
	private SignDao signDao;
	
	// 전자결재문서내용(SIGN_CONTENT 테이블) insert
	@Override
	public int insertSignContents(int sg_num, String sgf_id, HttpServletRequest req) {
		System.out.println("--Method insertSignContents in Class SignServiceImpl");
		int result = 0;
		
		// SIGN_FORM_COMPONENT 테이블로부터 각각의 sgfc_id에 대한 SignContent 객체 리스트 만들기
		List<SignContent> signContentList = signDao.selectListSignFormComponent(sgf_id);
		
		// signContent에 sgfc_id 값 제외한 나머지 값들 넣기
		for(SignContent signContent : signContentList) {
			signContent.setSg_num(sg_num);
			signContent.setSgf_id(sgf_id);
			signContent.setSgc_content(req.getParameter(signContent.getSgfc_id()) != null ? req.getParameter(signContent.getSgfc_id()) : "");
			
			// 파일 dv_id 넣기
			if (signContent.getSgfc_id().equals("dv_id")) {
				String dv_id = (String) req.getAttribute("dv_id");
				signContent.setSgc_content(dv_id != null ? dv_id : "");
			}
			
			System.out.println(signContent); //콘솔 확인
		}
		
		// SIGN_CONTENT 테이블에 insert all 하기
		result = signDao.insertSignContents(signContentList);
		
		return result;
	}


	@Override
	public int getSg_numOfNewSign(String m_id, String sgf_id) {
		System.out.println("-- com.example.sproject.service.sign.SignServiceImpl.getSg_numOfNewSign(String, String)");
		
		//max sg_num 찾기
		int sg_num = 1 + signDao.selectOneMaxSg_numOfSign();
		
		//SIGN 테이블에 insert하기
		signDao.insertSign(sg_num, m_id, sgf_id);
		
		//sg_num 리턴하기
		return sg_num;
	}


	@Override
	public int insertSignLines(List<SignLine> listOfSignLine) {
		System.out.println("-- com.example.sproject.service.sign.SignServiceImpl.insertSignLines(List<SignLine>)");
		int result = 0;
		result = signDao.insertSignLines(listOfSignLine);
		return result;
	}
	
	// 결재선 받은 input 리스트들 하나의 SignLine 객체 리스트로 바꾸기
	@Override
	public List<SignLine> convertToListOfSignLine(int sg_num, String[] listOfm_idOfSignLine, int[] listOfSgl_typeOfSignLine) {
		List<SignLine> listOfSignLine = new ArrayList<SignLine>();
		int sgl_order = 1; // iterator + 1
		for (String m_idInList : listOfm_idOfSignLine) {
			SignLine signLine = new SignLine();
			signLine.setSg_num(sg_num);
			signLine.setM_id(m_idInList);
			signLine.setSgl_type(listOfSgl_typeOfSignLine[sgl_order - 1]); // input에서 받은 sgl_type
			signLine.setSgl_status(0); // 결재대기중
			signLine.setSgl_order(sgl_order);
			++sgl_order;
			listOfSignLine.add(signLine);
		}
		return listOfSignLine;
	}

//리스트 가져오기
	@Override
	public List<Sign> listSignOfProposalProcessing(Sign sign) {
		List<Sign> listOfSignOfProposalProcessing = signDao.selectListSignOfProposalProcessing(sign);
		return listOfSignOfProposalProcessing;
	}
	
	@Override
	public List<Sign> listSignOfProposalCompleted(Sign sign) {
		List<Sign> listOfSignOfProposalCompleted = signDao.selectListSignOfProposalCompleted(sign);
		return listOfSignOfProposalCompleted;
	}
	
	@Override
	public List<Sign> listSignOfApprovalWaited(Sign sign) {
		List<Sign> listOfSignOfApprovalWaited = signDao.selectListSignOfApprovalWaited(sign);
		return listOfSignOfApprovalWaited;
	}

	@Override
	public List<Sign> listSignOfApprovalCompleted(Sign sign) {
		List<Sign> listOfSignOfApprovalCompleted = signDao.selectListSignOfApprovalCompleted(sign);
		return listOfSignOfApprovalCompleted;
	}
	
	

	@Override
	public Map<String, Object> findMapOfSignContent(int sg_num) {
		Map<String, Object> mapOfSignContent = new HashMap<String, Object>();
		List<SignContent> ListOfSignContent = signDao.selectListSignContent(sg_num);
		for(SignContent signContent : ListOfSignContent) {
			mapOfSignContent.put(signContent.getSgfc_id(), signContent.getSgc_content());
		}
		return mapOfSignContent;
	}


	@Override
	public Sign selectOneSign(int sg_num) {
		Sign sign = signDao.selectOneSign(sg_num);
		return sign;
	}


	@Override
	public List<Member> listMember() {
		List<Member> listOfMember = signDao.selectListMember();
		return listOfMember;
	}


	@Override
	public List<SignLine> findListOfSignLine(int sg_num) {
		return signDao.selectListSignLine(sg_num);
	}


	@Override
	public int countSignOfProposalProcessing(Sign sign) {
		return signDao.selectCountSignOfProposalProcessing(sign);
	}


	@Override
	public int countSignOfApprovalWaited(Sign sign) {
		return signDao.selectCountSignOfApprovalWaited(sign);
	}


	@Override
	public int countSignOfProposalCompleted(Sign sign) {
		return signDao.selectCountSignOfProposalCompleted(sign);
	}


	@Override
	public int countSignOfApprovalCompleted(Sign sign) {
		return signDao.selectCountSignOfApprovalCompleted(sign);
	}


	@Override
	public int approveSign(SignLine signLine) {
		System.out.println("-- com.example.sproject.service.sign.SignServiceImpl.approveSign(SignLine)");
		return signDao.approveSign(signLine);
	}


	@Override
	public List<SignForm> listSignForm() {
		return signDao.selectListSignForm();
	}

	/**
	 * 서명 이후 문서 상태 업데이트 하기
	 */
	@Override
	public int updateSignStatus(int sg_num) {
		List<SignLine> signLineList = findListOfSignLine(sg_num);
		int sg_status = GlobalsOfSg_status.COMPLETED;
		for (SignLine signLine : signLineList) {
			if (signLine.getSgl_status() == GlobalsOfSg_status.REJECTED) { // 반려 라인 있으면 문서 반려상태로 바로 업데이트 하기
				sg_status = GlobalsOfSg_status.REJECTED;
				return signDao.updateSignStatus(sg_num, sg_status);
			} else if (signLine.getSgl_status() == GlobalsOfSg_status.ONGOING) { // 진행중 라인 있으면 문서 진행상태로 돌려놓고 다른 라인들 마저 확인하기
				sg_status = GlobalsOfSg_status.ONGOING;
			}
		}
		// 반려 라인 없이 모든 라인 확인한 후에 문서 상태 업데이드하기
		return signDao.updateSignStatus(sg_num, sg_status);
	}





}
