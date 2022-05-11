package com.example.vto.vo2Po;

import com.example.pojo.Schedule;
import com.example.vto.vo.AddSchedule;
import com.example.vto.vo.EditSchedule;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface Schedule2PO {
    Schedule addSchedule2PO(AddSchedule addSchedule);

    Schedule editSchedule2PO(EditSchedule editSchedule);
}
