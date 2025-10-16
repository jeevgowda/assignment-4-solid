package edu.trincoll.service;

import edu.trincoll.model.Member;
import edu.trincoll.model.MembershipType;
import edu.trincoll.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Member Service Tests")
class MemberServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private MemberService memberService;

    private Member testMember;
    private List<Member> testMembers;

    @BeforeEach
    void setUp() {
        testMember = new Member("John Doe", "john@example.com");
        testMember.setId(1L);
        testMember.setMembershipType(MembershipType.REGULAR);
        testMember.setBooksCheckedOut(0);

        Member anotherMember = new Member("Jane Smith", "jane@example.com");
        anotherMember.setId(2L);
        anotherMember.setMembershipType(MembershipType.PREMIUM);
        anotherMember.setBooksCheckedOut(2);

        testMembers = List.of(testMember, anotherMember);
    }

    @Test
    @DisplayName("Should find member by email successfully")
    void shouldFindMemberByEmail() {
        // Arrange
        when(memberRepository.findByEmail(testMember.getEmail())).thenReturn(Optional.of(testMember));

        // Act
        Optional<Member> result = memberService.findByEmail(testMember.getEmail());

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo(testMember.getEmail());
        verify(memberRepository).findByEmail(testMember.getEmail());
    }

    @Test
    @DisplayName("Should return empty optional when member not found by email")
    void shouldReturnEmptyWhenMemberNotFoundByEmail() {
        // Arrange
        when(memberRepository.findByEmail("invalid@example.com")).thenReturn(Optional.empty());

        // Act
        Optional<Member> result = memberService.findByEmail("invalid@example.com");

        // Assert
        assertThat(result).isEmpty();
        verify(memberRepository).findByEmail("invalid@example.com");
    }

    @Test
    @DisplayName("Should find member by email or throw exception")
    void shouldFindMemberByEmailOrThrow() {
        // Arrange
        when(memberRepository.findByEmail(testMember.getEmail())).thenReturn(Optional.of(testMember));

        // Act
        Member result = memberService.findByEmailOrThrow(testMember.getEmail());

        // Assert
        assertThat(result).isEqualTo(testMember);
        verify(memberRepository).findByEmail(testMember.getEmail());
    }

    @Test
    @DisplayName("Should throw exception when member not found by email")
    void shouldThrowExceptionWhenMemberNotFoundByEmail() {
        // Arrange
        when(memberRepository.findByEmail("invalid@example.com")).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> memberService.findByEmailOrThrow("invalid@example.com"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Member not found");
        verify(memberRepository).findByEmail("invalid@example.com");
    }

    @Test
    @DisplayName("Should find all members")
    void shouldFindAllMembers() {
        // Arrange
        when(memberRepository.findAll()).thenReturn(testMembers);

        // Act
        List<Member> result = memberService.findAll();

        // Assert
        assertThat(result).hasSize(2);
        verify(memberRepository).findAll();
    }

    @Test
    @DisplayName("Should find members by membership type")
    void shouldFindMembersByMembershipType() {
        // Arrange
        List<Member> regularMembers = List.of(testMember);
        when(memberRepository.findByMembershipType(MembershipType.REGULAR)).thenReturn(regularMembers);

        // Act
        List<Member> result = memberService.findByMembershipType(MembershipType.REGULAR);

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getMembershipType()).isEqualTo(MembershipType.REGULAR);
        verify(memberRepository).findByMembershipType(MembershipType.REGULAR);
    }

    @Test
    @DisplayName("Should find members with checked out books")
    void shouldFindMembersWithCheckedOutBooks() {
        // Arrange
        List<Member> activeMembers = List.of(testMembers.get(1)); // Member with 2 books
        when(memberRepository.findByBooksCheckedOutGreaterThan(0)).thenReturn(activeMembers);

        // Act
        List<Member> result = memberService.findMembersWithCheckedOutBooks();

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getBooksCheckedOut()).isGreaterThan(0);
        verify(memberRepository).findByBooksCheckedOutGreaterThan(0);
    }

    @Test
    @DisplayName("Should save member")
    void shouldSaveMember() {
        // Arrange
        when(memberRepository.save(testMember)).thenReturn(testMember);

        // Act
        Member result = memberService.save(testMember);

        // Assert
        assertThat(result).isEqualTo(testMember);
        verify(memberRepository).save(testMember);
    }

    @Test
    @DisplayName("Should create new member with default membership type")
    void shouldCreateNewMemberWithDefaultMembershipType() {
        // Arrange
        String name = "New User";
        String email = "new@example.com";
        when(memberRepository.save(any(Member.class))).thenAnswer(invocation -> {
            Member member = invocation.getArgument(0);
            member.setId(3L);
            return member;
        });

        // Act
        Member result = memberService.createMember(name, email);

        // Assert
        assertThat(result.getName()).isEqualTo(name);
        assertThat(result.getEmail()).isEqualTo(email);
        assertThat(result.getMembershipType()).isEqualTo(MembershipType.REGULAR);
        assertThat(result.getBooksCheckedOut()).isEqualTo(0);
        verify(memberRepository).save(any(Member.class));
    }

    @Test
    @DisplayName("Should create new member with specific membership type")
    void shouldCreateNewMemberWithSpecificMembershipType() {
        // Arrange
        String name = "Premium User";
        String email = "premium@example.com";
        MembershipType membershipType = MembershipType.PREMIUM;
        when(memberRepository.save(any(Member.class))).thenAnswer(invocation -> {
            Member member = invocation.getArgument(0);
            member.setId(4L);
            return member;
        });

        // Act
        Member result = memberService.createMember(name, email, membershipType);

        // Assert
        assertThat(result.getName()).isEqualTo(name);
        assertThat(result.getEmail()).isEqualTo(email);
        assertThat(result.getMembershipType()).isEqualTo(membershipType);
        assertThat(result.getBooksCheckedOut()).isEqualTo(0);
        verify(memberRepository).save(any(Member.class));
    }

    @Test
    @DisplayName("Should update books checked out count")
    void shouldUpdateBooksCheckedOutCount() {
        // Arrange
        int newCount = 3;
        when(memberRepository.save(testMember)).thenReturn(testMember);

        // Act
        Member result = memberService.updateBooksCheckedOut(testMember, newCount);

        // Assert
        assertThat(testMember.getBooksCheckedOut()).isEqualTo(newCount);
        verify(memberRepository).save(testMember);
    }

    @Test
    @DisplayName("Should increment books checked out count")
    void shouldIncrementBooksCheckedOutCount() {
        // Arrange
        int initialCount = testMember.getBooksCheckedOut();
        when(memberRepository.save(testMember)).thenReturn(testMember);

        // Act
        Member result = memberService.incrementBooksCheckedOut(testMember);

        // Assert
        assertThat(testMember.getBooksCheckedOut()).isEqualTo(initialCount + 1);
        verify(memberRepository).save(testMember);
    }

    @Test
    @DisplayName("Should decrement books checked out count")
    void shouldDecrementBooksCheckedOutCount() {
        // Arrange
        testMember.setBooksCheckedOut(2);
        when(memberRepository.save(testMember)).thenReturn(testMember);

        // Act
        Member result = memberService.decrementBooksCheckedOut(testMember);

        // Assert
        assertThat(testMember.getBooksCheckedOut()).isEqualTo(1);
        verify(memberRepository).save(testMember);
    }

    @Test
    @DisplayName("Should not decrement below zero")
    void shouldNotDecrementBelowZero() {
        // Arrange
        testMember.setBooksCheckedOut(0);
        when(memberRepository.save(testMember)).thenReturn(testMember);

        // Act
        Member result = memberService.decrementBooksCheckedOut(testMember);

        // Assert
        assertThat(testMember.getBooksCheckedOut()).isEqualTo(0);
        verify(memberRepository).save(testMember);
    }

    @Test
    @DisplayName("Should update membership type")
    void shouldUpdateMembershipType() {
        // Arrange
        MembershipType newType = MembershipType.PREMIUM;
        when(memberRepository.save(testMember)).thenReturn(testMember);

        // Act
        Member result = memberService.updateMembershipType(testMember, newType);

        // Assert
        assertThat(testMember.getMembershipType()).isEqualTo(newType);
        verify(memberRepository).save(testMember);
    }

    @Test
    @DisplayName("Should delete member by ID")
    void shouldDeleteMemberById() {
        // Arrange
        Long memberId = 1L;

        // Act
        memberService.deleteById(memberId);

        // Assert
        verify(memberRepository).deleteById(memberId);
    }

    @Test
    @DisplayName("Should get member count")
    void shouldGetMemberCount() {
        // Arrange
        when(memberRepository.count()).thenReturn(10L);

        // Act
        long result = memberService.count();

        // Assert
        assertThat(result).isEqualTo(10L);
        verify(memberRepository).count();
    }

    @Test
    @DisplayName("Should check if member exists by email")
    void shouldCheckIfMemberExistsByEmail() {
        // Arrange
        when(memberRepository.findByEmail(testMember.getEmail())).thenReturn(Optional.of(testMember));

        // Act
        boolean result = memberService.existsByEmail(testMember.getEmail());

        // Assert
        assertThat(result).isTrue();
        verify(memberRepository).findByEmail(testMember.getEmail());
    }

    @Test
    @DisplayName("Should return false when member does not exist by email")
    void shouldReturnFalseWhenMemberDoesNotExistByEmail() {
        // Arrange
        when(memberRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

        // Act
        boolean result = memberService.existsByEmail("nonexistent@example.com");

        // Assert
        assertThat(result).isFalse();
        verify(memberRepository).findByEmail("nonexistent@example.com");
    }
}
