package com.example.peopoolbe.member.service;

import com.example.peopoolbe.member.api.dto.response.UserInfo;
import com.example.peopoolbe.member.api.dto.response.UserList;
import com.example.peopoolbe.member.domain.Member;
import com.example.peopoolbe.member.domain.ViewStatus;
import com.example.peopoolbe.member.domain.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OthersService {
    private final MemberRepository memberRepository;

    public UserList SearchOthers () {
        List<Member> userList = new ArrayList<>(memberRepository.findMemberByProfileVisible(ViewStatus.VISIBLE));

        List<UserInfo> userInfoList = userList.stream()
                .map(UserInfo::from)
                .toList();

        return UserList.fromUserInfo(userInfoList);
    }
}
