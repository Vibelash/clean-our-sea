package communities.communities;

import communities.communities.model.Community;
import communities.communities.model.Post;
import communities.communities.repository.CommunityRepository;
import communities.communities.repository.PostRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataLoader implements CommandLineRunner {

    private final CommunityRepository communityRepo;
    private final PostRepository postRepo;

    public DataLoader(CommunityRepository communityRepo, PostRepository postRepo){
        this.communityRepo = communityRepo;
        this.postRepo = postRepo;
    }

    @Override
    public void run(String... args) throws Exception {
        if(communityRepo.count() > 0) return;

        Community c1 = new Community("Brighton Coastal Volunteers","Local volunteers coordinating beach cleanups and trash pickups across Brighton shoreline.",124,List.of("cleanup"));
        Community c2 = new Community("Marine Research Network","Researchers sharing data on microplastics, species impact, and survey results.",86,List.of("research"));
        Community c3 = new Community("Policy & Advocacy","Groups coordinating advocacy campaigns and local policy pushes to reduce single-use plastics.",42,List.of("policy"));

        communityRepo.saveAll(List.of(c1,c2,c3));

        // seed sample posts with fake authors
        String[] authors = {"Alice", "Bob", "Charlie", "Diana", "Eve"};
        String[] volunteersMessages = {
            "Just finished a cleanup on Brighton Beach. Collected 3 bags of trash!",
            "Anyone free this Saturday for the coastal cleanup?",
            "Great turnout at the last event. Let's make it a weekly thing!",
            "We should focus on the area near the pier next.",
            "Thanks everyone for the hard work. Our beach is getting cleaner!"
        };
        String[] researchMessages = {
            "New study on microplastics in seawater just published. Check the latest findings!",
            "Has anyone encountered unusual species patterns lately?",
            "We're looking for volunteers to help with water sampling next month.",
            "The data from last quarter shows a concerning trend...",
            "Collaborative analysis with the EU team is showing promising results."
        };
        String[] policyMessages = {
            "The new single-use plastic ban takes effect next month!",
            "Council meeting is scheduled for March 15th. Everyone welcome.",
            "We've submitted our proposal to the local government.",
            "Corporate partnerships are critical to making real change.",
            "Let's discuss strategy for the upcoming campaign."
        };

        for (int i = 0; i < 5; i++) {
            postRepo.save(new Post(c1, authors[i], volunteersMessages[i]));
            postRepo.save(new Post(c2, authors[i], researchMessages[i]));
            postRepo.save(new Post(c3, authors[i], policyMessages[i]));
        }
    }
}
