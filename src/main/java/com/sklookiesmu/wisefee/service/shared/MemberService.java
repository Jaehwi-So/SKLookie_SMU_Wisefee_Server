package com.sklookiesmu.wisefee.service.shared;

import com.sklookiesmu.wisefee.domain.Member;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface MemberService {

    /**
     * [회원 가입]
     * 회원 정보를 입력받아, 회원가입시킨다.
     * @param [member 회원 엔티티 모델(id=null)]
     * @return [성공 시 회원 PK 반환]
     * @throws [예외 타입1] [예외 설명]
     */
    public abstract Long join(Member member);


    /**
     * [회원 리스트 조회]
     * 회원 전체 리스트를 조회한다.
     *
     * @param [order 정렬 순서(asc/desc)]
     * @return [회원 전체 리스트 반환]
     */
    public abstract List<Member> getMembers(String order);


    /**
     * [회원 조회]
     * 회원을 PK를 통해서 조회한다.
     * @param [id 회원 엔티티 PK]
     * @return [회원 반환]
     */
    public abstract Member getMember(Long id);

    /**
     * [Member 엔티티 수정]
     * 이메일을 통해 회원 정보를 수정한다.
     * @param [email 회원 이메일]
     * @param [Member Member 엔티티(수정사항 반영된 상태)]
     * @return [id (실행결과)]
     */
    public abstract Long updateMember(Long id, Member updateMember);

    /**
     * [Member 엔티티 비밀번호 수정]
     * 회원 Id를 통해 비밀번호를 수정한다.
     * @param [Id 회원 PK]
     * @param [Member Member 엔티티(수정사항 반영된 상태)]
     * @return [id (실행결과)]
     */
    public abstract Long updatePasswordAsMember(Long id, Member updateMember);

    /**
     * [멤버 조회]
     * email을 이용해 회원 정보를 조회한다.
     * 조회하는 정보는 회원가입 시 입력한 정보와 동일
     *
     * @param email
     * @return
     */
    public abstract Optional<Member> getMemberByEmail(String email);


    /**
     * [OAuth 구글 회원 가입]
     * OAuth Google 계정을 통해 가입시킨다.
     * @param [member 회원 엔티티 모델(id=null)]
     * @return [성공 시 회원 PK 반환]
     */
    public abstract Long joinGoogle(Member member, String accessToken) throws IOException;

}