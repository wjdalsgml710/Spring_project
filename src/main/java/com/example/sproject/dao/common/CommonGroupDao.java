package com.example.sproject.dao.common;

import java.util.List;

import com.example.sproject.model.common.CommonGroup;

public interface CommonGroupDao {
	List<CommonGroup> selectList(String tb_code);

	int selectOneInsertedCg_order(CommonGroup commonGroup);

	int selectOneMaxCg_order(CommonGroup commonGroup);

	int pushCg_order(CommonGroup commonGroup);

	int insertCommonGroup(CommonGroup commonGroup);

	int selectOneMaxCg_num(String tb_code);

	CommonGroup selectOneParentCommonGroup(CommonGroup commonGroup);

	List<CommonGroup> selectList(String tb_code, int cg_ref, int cg_depth);

	int deleteCommonGroup(CommonGroup commonGroup);

	int updateCommonGroup(CommonGroup commonGroup);

	CommonGroup selectOneCommonGroup(CommonGroup commonGroup);

	List<CommonGroup> selectList(String tb_code, int cg_ref);
}
