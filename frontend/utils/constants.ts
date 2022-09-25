import { ReviewStatus } from "../types";

export const subjects = ["Art", "Biology", "Business", "Chemistry", "Computer Science", "Earth Science", "Economics", "Education", "Engineering", "History", "Mathematics", "Philosophy", "Political Science", "Physics", "Psychology"];
export const subsubjects: Record<string, string[]> = {
  "Art": ["Architecture", "Cinema", "Literature", "Music", "Painting", "Performance", "Sculpture"],
  "Biology": ["Genetics", "Medicine"],
  "Business": ["Finance", "Human Resources", "Marketing", "Operations", "Strategy"],
  "Chemistry": ["Biochemistry", "Inorganic Chemistry", "Organic Chemistry", "Physical Chemistry"],
  "Computer Science": ["Algorithms", "Artificial Intelligence", "Networks", "Cybersecurity", "Data Structures", "Programming Languages"],
  "Earth Science": ["Astronomy", "Geology", "Oceanography", "Meteorology"],
  "Economics": ["Macroeconomics", "Microeconomics"],
  "Education": ["Primary Education", "Secondary Education", "Special Education", "Vocational Education"],
  "Engineering": ["Chemical Engineering", "Civil Engineering", "Electrical Engineering", "Mechanical Engineering"],
  "History": ["Cultural History", "Economic History", "Political History", "Social History"],
  "Mathematics": ["Arithmetic", "Algebra", "Calculus", "Geometry", "Logic", "Number Theory", "Probability"],
  "Philosophy": ["Aesthetics", "Epistemology", "Ethics", "Metaphysics"],
  "Political Science": ["Political Law", "Political Theory"],
  "Physics": ["Mechanics", "Nuclear Physics"],
  "Psychology": ["Behavioral Psychology", "Clinical Psychology", "Cognitive Psychology"],
};

export const allStatuses: ReviewStatus[] = ["Approved", "Needs Work", "Rejected"];