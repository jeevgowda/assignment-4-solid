package edu.trincoll.service;

import edu.trincoll.model.Member;
import edu.trincoll.model.MembershipType;
import edu.trincoll.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service responsible for member management operations.
 * Follows Single Responsibility Principle - handles only member-related operations.
 */
@Service
@Transactional
public class MemberService {

    private final MemberRepository memberRepository;

    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    /**
     * Find a member by email
     */
    public Optional<Member> findByEmail(String email) {
        return memberRepository.findByEmail(email);
    }

    /**
     * Find a member by email or throw exception if not found
     */
    public Member findByEmailOrThrow(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));
    }

    /**
     * Get all members
     */
    public List<Member> findAll() {
        return memberRepository.findAll();
    }

    /**
     * Get members by membership type
     */
    public List<Member> findByMembershipType(MembershipType membershipType) {
        return memberRepository.findByMembershipType(membershipType);
    }

    /**
     * Get members who have checked out books
     */
    public List<Member> findMembersWithCheckedOutBooks() {
        return memberRepository.findByBooksCheckedOutGreaterThan(0);
    }

    /**
     * Save a member
     */
    public Member save(Member member) {
        return memberRepository.save(member);
    }

    /**
     * Create a new member
     */
    public Member createMember(String name, String email) {
        Member member = new Member(name, email);
        return memberRepository.save(member);
    }

    /**
     * Create a new member with specific membership type
     */
    public Member createMember(String name, String email, MembershipType membershipType) {
        Member member = new Member(name, email, membershipType);
        return memberRepository.save(member);
    }

    /**
     * Update member's books checked out count
     */
    public Member updateBooksCheckedOut(Member member, int newCount) {
        member.setBooksCheckedOut(newCount);
        return memberRepository.save(member);
    }

    /**
     * Increment member's books checked out count
     */
    public Member incrementBooksCheckedOut(Member member) {
        member.setBooksCheckedOut(member.getBooksCheckedOut() + 1);
        return memberRepository.save(member);
    }

    /**
     * Decrement member's books checked out count
     */
    public Member decrementBooksCheckedOut(Member member) {
        int currentCount = member.getBooksCheckedOut();
        if (currentCount > 0) {
            member.setBooksCheckedOut(currentCount - 1);
        }
        return memberRepository.save(member);
    }

    /**
     * Update member's membership type
     */
    public Member updateMembershipType(Member member, MembershipType membershipType) {
        member.setMembershipType(membershipType);
        return memberRepository.save(member);
    }

    /**
     * Delete a member
     */
    public void deleteById(Long id) {
        memberRepository.deleteById(id);
    }

    /**
     * Get total member count
     */
    public long count() {
        return memberRepository.count();
    }

    /**
     * Check if member exists by email
     */
    public boolean existsByEmail(String email) {
        return memberRepository.findByEmail(email).isPresent();
    }
}
