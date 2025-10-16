package edu.trincoll.service.api;

import edu.trincoll.model.Member;

/**
 * Interface defining member service operations
 */
public interface IMemberService {
    Member findByEmailOrThrow(String email);
    void incrementBooksCheckedOut(Member member);
    void decrementBooksCheckedOut(Member member);
    Member addMember(String email, String name, String membershipType);
    void removeMember(String email);
}