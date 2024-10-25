package tn.esprit.spring.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.spring.entities.Course;
import tn.esprit.spring.entities.Support;
import tn.esprit.spring.entities.TypeCourse;
import tn.esprit.spring.repositories.ICourseRepository;
import tn.esprit.spring.services.CourseServicesImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
 class CourseServiceImplMockTest {

    @Mock
    ICourseRepository courseRepository;

    @InjectMocks
    CourseServicesImpl courseService;

    // Sample Course object setup for testing
    Course course = new Course(1L, 1, TypeCourse.COLLECTIVE_CHILDREN, Support.SKI, 100.0f, 2, null);

    List<Course> listcourse = new ArrayList<Course>() {
        {
            add(new Course(2L, 2, TypeCourse.COLLECTIVE_CHILDREN, Support.SKI, 150.0f, 3, null));
            add(new Course(3L, 3, TypeCourse.COLLECTIVE_CHILDREN, Support.SKI, 200.0f, 4, null));
        }
    };

    @Test
     void testRetrieveCourse() {
        // Mocking the repository to return the sample course when findById is called
        Mockito.when(courseRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(course));

        // Calling the service method
        Course course1 = courseService.retrieveCourse(1L);

        // Verifying the returned object is not null
        Assertions.assertNotNull(course1);
    }
}
