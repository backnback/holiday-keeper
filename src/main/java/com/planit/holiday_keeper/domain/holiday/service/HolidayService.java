package com.planit.holiday_keeper.domain.holiday.service;

import com.planit.holiday_keeper.domain.holiday.repository.HolidayRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class HolidayService {

  private final HolidayRepository holidayRepository;



}
